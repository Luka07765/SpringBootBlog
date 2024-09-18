package com.example.blog.dto;

import lombok.Data;

@Data  // Lombok annotation to generate getters, setters, equals, hashCode, and toString
public class LoginUserDto {
    private String email;
    private String password;
}
