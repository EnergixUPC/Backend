package com.backendsems.iam.infrastructure.hashing.bcrypt.services;

import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt Hashing Service Implementation.
 * <p>
 * This class implements the HashingService and BCryptHashingService interfaces.
 * It uses BCryptPasswordEncoder for hashing passwords.
 * </p>
 */
@Service
public class HashingServiceImpl implements HashingService, BCryptHashingService {

    private final BCryptPasswordEncoder passwordEncoder;

    public HashingServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}