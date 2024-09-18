package com.example.blog.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.blog.model.ERole;
import com.example.blog.model.Role;
import com.example.blog.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Insert ROLE_ADMIN if it doesn't exist
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        // Insert ROLE_USER if it doesn't exist
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
        }
    }
}
