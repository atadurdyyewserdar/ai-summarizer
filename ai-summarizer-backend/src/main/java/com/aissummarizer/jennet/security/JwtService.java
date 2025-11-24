package com.aissummarizer.jennet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Handles JWT creation and validation using JJWT 0.13.x API.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    @PostConstruct
    private void initKey() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getKey())
                .compact();
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getKey())     // NEW JJWT 0.13.x syntax
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean isExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        return expectedUsername.equals(extractUsername(token)) && !isExpired(token);
    }

    /**
     * Validates a REFRESH TOKEN and extracts the username.
     *
     * @param refreshToken raw refresh token sent by client
     * @return username if valid
     * @throws JwtException if verification fails
     */
    public String validateAndExtractUsernameFromRefreshToken(String refreshToken) {
        // ✔ Refresh tokens have same structure, only expiration differs.
        return extractUsername(refreshToken);
    }
}