package com.backendsems.iam.infrastructure.authorization.sfs.pipeline;

import com.backendsems.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.backendsems.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bearer Authorization Request Filter.
 * <p>
 * This class is responsible for filtering requests and setting the user authentication.
 * It extends the OncePerRequestFilter class.
 * </p>
 * @see OncePerRequestFilter
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);
    private final BearerTokenService tokenService;


    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        System.out.println("BearerAuthorizationRequestFilter created successfully");
    }

    /**
     * This method is responsible for filtering requests and setting the user authentication.
     * @param request The request object.
     * @param response The response object.
     * @param filterChain The filter chain object.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Filter doFilterInternal called for: " + request.getRequestURI());
        try {
            System.out.println("=== FILTER EXECUTING FOR: " + request.getRequestURI() + " ===");
            LOGGER.info("Processing request to: {}", request.getRequestURI());
            
            String authHeader = request.getHeader("Authorization");
            System.out.println("Authorization Header: " + authHeader);
            
            String token = tokenService.getBearerTokenFrom(request);
            System.out.println("Token extracted: " + (token != null ? "Present (length: " + token.length() + ")" : "Missing"));
            LOGGER.info("Token extracted: {}", token != null ? "Present" : "Missing");
            
            if (token != null && tokenService.validateToken(token)) {
                String username = tokenService.getUsernameFromToken(token);
                System.out.println("Username from token: " + username);
                LOGGER.info("Username from token: {}", username);
                var userDetails = userDetailsService.loadUserByUsername(username);
                LOGGER.info("User details loaded: {}", userDetails.getUsername());
                SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
                System.out.println("Authentication set successfully for user: " + username);
                LOGGER.info("Authentication set successfully for user: {}", username);
            } else {
                System.out.println("Token validation FAILED - Token is " + (token == null ? "null" : "invalid"));
                LOGGER.warn("Token validation failed - Token is null or invalid");
            }

        } catch (Exception e) {
            System.out.println("ERROR in filter: " + e.getMessage());
            e.printStackTrace();
            LOGGER.error("Cannot set user authentication: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}