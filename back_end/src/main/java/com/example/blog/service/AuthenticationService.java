package com.example.blog.service;
import org.springframework.context.annotation.Lazy;
import com.example.blog.model.Role;
import com.example.blog.model.ERole;

import com.example.blog.repository.RoleRepository;
import com.example.blog.dto.LoginUserDto;
import com.example.blog.dto.RegisterUserDto;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository, // Add RoleRepository to handle roles
            @Lazy AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registracija korisnika (signup)
    public User signup(RegisterUserDto input) {

        userRepository.findByEmail(input.getEmail())
                .ifPresent(user -> {
                    throw new IllegalStateException("Email already taken");
                });

        // Kreiraj novog korisnika
        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setUsername(input.getUsername());

        // Dodela default role (ROLE_USER)
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        user.getRoles().add(userRole); // Dodaj rolu korisniku

        // Sačuvaj korisnika u repozitorijum
        return userRepository.save(user);
    }

    // Autentifikacija korisnika (login)
    public User authenticate(LoginUserDto input) {
        // Validacija email-a i lozinke
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // Učitavanje korisnika iz baze prema email-u
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input.getEmail()));
    }

    // Učitavanje korisnika prema email-u (za Spring Security)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Trying to load user by email: " + email);
        // Pretraga korisnika prema email-u
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Vraćanje korisničkih podataka (koristi Spring Security UserDetails)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}