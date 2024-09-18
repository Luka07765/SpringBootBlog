package com.example.blog.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshTokenExpiration;

    // Extract email from the token (instead of username)
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);  // The subject of the token will now be the email
    }

    // Extract specific claims from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate a JWT token for the user (using email as the identifier)
    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    // Generate a refresh token for the user
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    // Build a token with claims, expiration, and user details (email as the subject)
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        // Add roles to the token
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));

        // Store the email in the subject of the JWT
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())  // Assuming userDetails.getUsername() returns the email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate if the token is still valid and not expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);  // Extract email instead of username
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);  // Compare email to userDetails
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Get the signing key using the secret key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Optional: Return the expiration time
    public long getExpirationTime() {
        return jwtExpiration;
    }
}
