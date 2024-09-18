package com.example.blog.controller;
import com.example.blog.model.ERole;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.blog.model.Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.blog.dto.LoginUserDto;
import com.example.blog.dto.RegisterUserDto;
import com.example.blog.dto.LoginResponse;
import com.example.blog.model.User;
import com.example.blog.service.AuthenticationService;
import com.example.blog.service.JwtService;
import com.example.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@CrossOrigin("*")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    // User registration

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Generate JWT and Refresh token
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        // Fetch user roles
        List<String> roles = authenticatedUser.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        // Create response with both tokens and user details
        LoginResponse loginResponse = new LoginResponse()
                .setId(authenticatedUser.getId())
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setEmail(authenticatedUser.getEmail())
                .setFullName(authenticatedUser.getFullName())
                .setUsername(authenticatedUser.getUsername())
                .setRoles(roles);  // Include roles in the response

        return ResponseEntity.ok(loginResponse);
    }


    // Admin login
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> authenticateAdmin(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedAdmin = authenticationService.authenticate(loginUserDto);

        // Verify if the user has the ADMIN role
        if (authenticatedAdmin.getRoles().stream().noneMatch(role -> role.getName().name().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(null); // Forbidden if not admin
        }

        // Generate JWT and Refresh token for the admin
        String jwtToken = jwtService.generateToken(authenticatedAdmin);
        String refreshToken = jwtService.generateRefreshToken(authenticatedAdmin);

        
        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setEmail(authenticatedAdmin.getEmail())
                .setFullName(authenticatedAdmin.getFullName());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // Check if the refresh token is expired
        if (jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            // Extract username from refresh token
            String username = jwtService.extractEmail(refreshToken);
            UserDetails userDetails = authenticationService.loadUserByUsername(username);

            // Generate new JWT token and refresh token
            String newJwtToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);  // Generate new refresh token as well

            // Prepare response with both tokens
            LoginResponse response = new LoginResponse()
                    .setToken(newJwtToken)
                    .setRefreshToken(newRefreshToken)  // Send the new refresh token
                    .setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // In case something goes wrong, return a 403 (Forbidden)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // Admin registration endpoint, restricted to admins
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<User> registerAdmin(@RequestBody RegisterUserDto registerUserDto) {
        User registeredAdmin = userService.registerAdmin(
                registerUserDto.getUsername(),
                registerUserDto.getEmail(),
                registerUserDto.getPassword(),
                registerUserDto.getFullName()
        );
        return ResponseEntity.ok(registeredAdmin);
    }
}
