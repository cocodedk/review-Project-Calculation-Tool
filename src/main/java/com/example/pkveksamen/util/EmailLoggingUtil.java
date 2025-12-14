package com.example.pkveksamen.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for safely logging email addresses without exposing PII.
 * Uses SHA-256 hashing with a fixed application-wide salt to create
 * non-identifying identifiers for logging purposes.
 */
public class EmailLoggingUtil {

    /**
     * Fixed application-wide salt for email hashing.
     * In production, this should be stored securely (e.g., environment variable or secret store).
     */
    private static final String EMAIL_HASH_SALT = "PkvEksamen-Email-Logging-Salt-2024";

    /**
     * Creates a non-identifying log-safe identifier from an email address.
     * Returns a format: username:email_hash where email_hash is the first 8 characters
     * of the SHA-256 hash of the email combined with the application salt.
     *
     * @param username The username associated with the email
     * @param email The email address to hash (will not be logged in plain text)
     * @return A log-safe identifier string in format "username:email_hash"
     */
    public static String createLogSafeEmailIdentifier(String username, String email) {
        if (email == null || email.isEmpty()) {
            return username + ":<no-email>";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedEmail = EMAIL_HASH_SALT + email.toLowerCase().trim();
            byte[] hashBytes = digest.digest(saltedEmail.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string and take first 8 characters for readability
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String emailHash = hexString.substring(0, 8);
            return username + ":" + emailHash;
        } catch (NoSuchAlgorithmException e) {
            // Fallback to masked email if hashing fails (should never happen with SHA-256)
            return maskEmail(username, email);
        }
    }

    /**
     * Fallback method to mask email if hashing fails.
     * Masks the email by showing only the first character and domain.
     *
     * @param username The username
     * @param email The email to mask
     * @return A masked email identifier
     */
    private static String maskEmail(String username, String email) {
        if (email == null || email.isEmpty()) {
            return username + ":<no-email>";
        }

        String normalizedEmail = email.toLowerCase().trim();
        int atIndex = normalizedEmail.indexOf('@');

        if (atIndex <= 0 || atIndex >= normalizedEmail.length() - 1) {
            return username + ":<invalid-email>";
        }

        String localPart = normalizedEmail.substring(0, atIndex);
        String domain = normalizedEmail.substring(atIndex + 1);

        // Show first char of local part and full domain hash
        char firstChar = localPart.charAt(0);
        String domainHash = hashString(domain).substring(0, 6);

        return username + ":" + firstChar + "***@" + domainHash;
    }

    /**
     * Simple hash helper for fallback masking.
     */
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "hash-error";
        }
    }
}

