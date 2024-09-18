package com.example.blog.security;

import com.example.blog.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter is triggered");

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        // Check if the Authorization header is present and starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the Authorization header
        jwtToken = authHeader.substring(7);  // Remove 'Bearer ' prefix
        try {
            // Extract the email from the token
            userEmail = jwtService.extractEmail(jwtToken);  // Change this to extractEmail

            System.out.println("Extracted User Email from Token: " + userEmail);

            // Check if the email is present and there's no existing authentication
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details using the email
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                System.out.println("Loaded User Details for Email: " + userEmail);

                // Validate the token with the user's details
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Handle exceptions related to JWT validation
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}