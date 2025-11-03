package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.valueobjects.LoginCredentials;
import com.backendsems.SEMS.domain.model.valueobjects.TokenPair;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    
    public TokenPair authenticate(LoginCredentials credentials) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
            )
        );
        
        UserAggregate user = userRepository.findByUsername(credentials.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = tokenService.generateToken(user);
        return new TokenPair(token);
    }
    
    public UserAggregate register(UserAggregate user) {
        if (userRepository.existsByUsername(user.getEmail().getValue())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail().getValue())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public UserAggregate getCurrentUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}