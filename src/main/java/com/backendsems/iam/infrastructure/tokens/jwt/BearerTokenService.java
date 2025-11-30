package com.backendsems.iam.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Bearer Token Service.
 * <p>
 * This interface defines the methods for handling JWT tokens.
 * </p>
 */
public interface BearerTokenService {

    /**
     * Generate a token for the given username.
     * @param username the username
     * @return the token
     */
    String generateToken(String username);

    /**
     * Validate the token.
     * @param token the token
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Get the username from the token.
     * @param token the token
     * @return the username
     */
    String getUsernameFromToken(String token);

    /**
     * Get the bearer token from the request.
     * @param request the request
     * @return the token
     */
    String getBearerTokenFrom(HttpServletRequest request);
}