package com.backendsems.iam.infrastructure.hashing.bcrypt;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt Hashing Service.
 * <p>
 * This interface defines the methods for hashing passwords using BCrypt.
 * It extends PasswordEncoder for Spring Security compatibility.
 * </p>
 */
public interface BCryptHashingService extends PasswordEncoder {

    /**
     * Encodes the raw password.
     * @param rawPassword the raw password
     * @return the encoded password
     */
    String encode(CharSequence rawPassword);

    /**
     * Matches the raw password with the encoded password.
     * @param rawPassword the raw password
     * @param encodedPassword the encoded password
     * @return true if matches, false otherwise
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}