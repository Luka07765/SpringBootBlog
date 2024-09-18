package com.example.blog.dto;

import lombok.Data;

@Data  // Lombok annotation to generate getters, setters, equals, hashCode, and toString
public class RegisterUserDto {
    private String email;
    private String password;
    private String fullName;
    private String username;

}
