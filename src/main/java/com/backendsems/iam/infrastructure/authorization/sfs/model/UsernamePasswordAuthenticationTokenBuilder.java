package com.backendsems.iam.infrastructure.authorization.sfs.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Username Password Authentication Token Builder.
 * <p>
 * This class is responsible for building the UsernamePasswordAuthenticationToken.
 * </p>
 */
public class UsernamePasswordAuthenticationTokenBuilder {

    /**
     * This method builds the UsernamePasswordAuthenticationToken.
     * @param userDetails The user details
     * @param request The request
     * @return The UsernamePasswordAuthenticationToken
     */
    public static UsernamePasswordAuthenticationToken build(UserDetails userDetails, HttpServletRequest request) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}