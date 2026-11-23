package com.lynkster.backend.common.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class VerificationTokenGenerator {

    private static final int TOKEN_LENGTH = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a secure random token for email verification
     */
    public String generateToken() {

        // Create an array to hold the random bytes
        byte[] bytes = new byte[TOKEN_LENGTH];

        // Fill the array with cryptographically secure random bytes
        secureRandom.nextBytes(bytes);

        // Encode the token so it can safely be included in a URL
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashes a verification token using SHA-256
     */
    public String hashToken(String token) {
        try {

            // Create a SHA-256 message digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hash the token using UTF-8 encoding
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convert the hash into a hexadecimal string
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {

            // Stop token processing if SHA-256 is unavailable
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }
}
