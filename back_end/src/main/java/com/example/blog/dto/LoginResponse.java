package com.example.blog.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LoginResponse {
    private String token;
    private String refreshToken;  // Refresh token field
    private long expiresIn;

    // Add user data fields (such as email, username)
    private String email;
    private String fullName;
    private String username;
    private Long id;
    private List<String> roles;
}
