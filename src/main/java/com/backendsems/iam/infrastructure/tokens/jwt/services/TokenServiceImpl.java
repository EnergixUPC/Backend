package com.backendsems.iam.infrastructure.tokens.jwt.services;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.iam.domain.model.entities.RevokedToken;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.RevokedTokenRepository;
import com.backendsems.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Bearer Token Service Implementation.
 * This class implements the TokenService and BearerTokenService interfaces using JWT.
 */
@Service
public class TokenServiceImpl implements TokenService, BearerTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Value("${authorization.jwt.secret}")
    private String jwtSecret;

    @Value("${authorization.jwt.expiration}")
    private int jwtExpirationMs;

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenServiceImpl(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    public String getEmailFromToken(String token) {
        return getUsernameFromToken(token);
    }

    @Override
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null) {
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            if (bearerToken.split("\\.").length == 3) {
                return bearerToken;
            }
        }
        return null;
    }

    @Override
    public void revokeToken(String token) {
        if (!isTokenRevoked(token)) {
            revokedTokenRepository.save(new RevokedToken(token));
        }
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
