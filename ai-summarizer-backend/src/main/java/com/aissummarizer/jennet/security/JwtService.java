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

    private SecretKey signingKey;

    @PostConstruct
    private void initKey() {
        // MUST be SecretKey type for verifyWith()
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)   // NEW JJWT 0.13.x style
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)  // NEW verifyWith()
                .build()
                .parseSignedClaims(token)  // NEW parseSignedClaims()
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token, String expectedUsername) {
        try {
            String actual = extractUsername(token);
            return actual.equals(expectedUsername);
        } catch (Exception ignored) {
            return false;
        }
    }
}