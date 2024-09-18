package com.example.blog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())  // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/**").permitAll()  // Public APIs (e.g., registration, login)
//                        .requestMatchers("/uploads/**").permitAll()  // Allow public access to uploads directory
//                        .requestMatchers("/auth/**").permitAll()  // Allow public access to auth endpoints
//                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Only accessible by admins
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .requestMatchers("/api/posts/**/likes").hasRole("USER")// Only accessible by logged-in users
//                        .anyRequest().authenticated()  // All other requests require authentication
                                .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session (JWT)
                )
                .authenticationProvider(authenticationProvider)  // Set custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));  // Allow all origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS")); // Specifične metode
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));// Allow all headers
        configuration.setAllowCredentials(true);  // Allow credentials like cookies or tokens
// Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply CORS configuration to all endpoints

        return source;
    }
}





















//                        .requestMatchers("/api/**").permitAll()  // Public APIs (e.g., registration, login)
//                        .requestMatchers("/uploads/**").permitAll()  // Allow public access to uploads directory
//                        .requestMatchers("/auth/**").permitAll()  // Allow public access to auth endpoints
//                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Only accessible by admins
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .requestMatchers("/api/posts/**/likes").hasRole("USER")// Only accessible by logged-in users
//                        .anyRequest().authenticated()  // All other requests require authentication