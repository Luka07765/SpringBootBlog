package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.model.Role;
import com.example.blog.model.ERole;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }



    // User registration logic
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already taken!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Assign ROLE_USER by default
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }

    // Admin registration logic
    public User registerAdmin(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already taken!");
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFullName(fullName);


        // Assign ROLE_ADMIN
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        admin.setRoles(Collections.singleton(adminRole));

        return userRepository.save(admin);
    }
}
