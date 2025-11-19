package com.aissummarizer.jennet.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating cryptographically secure random tokens.
 * <p>
 * Used for password reset tokens and other places where unpredictable,
 * unguessable tokens are required.
 */
@Component
public class SecureTokenGenerator {

    /** Default token byte length (32 bytes = 256 bits of entropy). */
    private static final int DEFAULT_BYTE_LENGTH = 32;

    /** SecureRandom is designed for cryptographic operations. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random token using the default secure byte length.
     *
     * @return a URL-safe Base64 encoded secure random token
     */
    public String generateToken() {
        return generateToken(DEFAULT_BYTE_LENGTH);
    }

    /**
     * Generates a cryptographically secure random token.
     * <p>
     * The token is encoded using URL-safe Base64 without padding
     * so that it can safely be sent via email, URLs, or JSON.
     *
     * @param byteLength number of random bytes (higher = more secure)
     * @return generated token
     */
    public String generateToken(int byteLength) {
        byte[] bytes = new byte[byteLength];

        // 🔐 Tricky logic:
        // SecureRandom provides non-deterministic random bytes suitable
        // for cryptographic operations (unlike Random).
        secureRandom.nextBytes(bytes);

        // URL-safe Base64 ensures the token has no "+" or "/" characters.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
