package com.yash.cabinbooking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PASSWORD UTILITY CLASS - SECURE HASHING WITH BCRYPT
 *
 * Features:
 * - BCrypt hashing with automatic salt generation
 * - Secure password verification
 * - Industry-standard security practices
 * - Protection against rainbow table attacks
 */
public class PasswordUtil {

    // BCrypt work factor (cost) - Higher = More secure but slower
    // 12 is recommended for 2024 security standards
    private static final int WORK_FACTOR = 12;

    /**
     * Hash a plain text password using BCrypt
     * @param plainTextPassword The plain text password to hash
     * @return BCrypt hashed password with salt
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
            System.out.println("🔐 Password hashed successfully with BCrypt (work factor: " + WORK_FACTOR + ")");
            return hashedPassword;
        } catch (Exception e) {
            System.err.println("❌ Error hashing password: " + e.getMessage());
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verify a plain text password against a BCrypt hash
     * @param plainTextPassword The plain text password to verify
     * @param hashedPassword The BCrypt hash to verify against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            System.err.println("❌ Password verification failed: null input");
            return false;
        }

        if (plainTextPassword.trim().isEmpty() || hashedPassword.trim().isEmpty()) {
            System.err.println("❌ Password verification failed: empty input");
            return false;
        }

        try {
            boolean matches = BCrypt.checkpw(plainTextPassword, hashedPassword);
            System.out.println("🔍 Password verification: " + (matches ? "✅ SUCCESS" : "❌ FAILED"));
            return matches;
        } catch (Exception e) {
            System.err.println("❌ Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a string is a BCrypt hash
     * @param password The string to check
     * @return true if it's a BCrypt hash, false otherwise
     */
    public static boolean isBCryptHash(String password) {
        if (password == null || password.length() < 60) {
            return false;
        }

        // BCrypt hash format: $2a$rounds$salt+hash (60 chars total)
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    /**
     * Generate a random secure password (for testing or admin use)
     * @param length Password length (minimum 8)
     * @return Random secure password
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) {
            length = 8;
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets security requirements
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Get password strength description
     * @param password Password to analyze
     * @return Strength description
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Too Short";
        }

        if (password.length() < 8) {
            return "Weak";
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        if (password.length() >= 12) score++;

        switch (score) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Moderate";
            case 4:
                return "Strong";
            case 5:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }
}
