package com.backendsems.iam.domain.model.services;

import com.backendsems.SEMS.domain.model.entities.User;
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
        System.out.println("Authenticating user: " + credentials.getUsername());
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
            )
        );
        
        User user = userRepository.findByEmailOrUsername(credentials.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = tokenService.generateToken(user);
        return new TokenPair(token);
    }
    
    public User register(User user) {
        System.out.println("Registering user: " + user.getEmail());
        
        if (userRepository.existsByUsername(user.getEmail())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Establecer username igual a email si no se proporciona
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User getCurrentUser(String username) {
        return userRepository.findByEmailOrUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}