package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.valueobjects.LoginCredentials;
import com.backendsems.SEMS.domain.model.valueobjects.TokenPair;
import com.backendsems.SEMS.domain.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginCredentials credentials) {
        try {
            TokenPair token = authenticationService.authenticate(credentials);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            UserAggregate userAggregate = user.toAggregate();
            UserAggregate registeredAggregate = authenticationService.register(userAggregate);
            User registeredUser = User.fromAggregate(registeredAggregate);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        try {
            UserAggregate userAggregate = authenticationService.getCurrentUser(authentication.getName());
            User user = User.fromAggregate(userAggregate);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(Authentication authentication) {
        return ResponseEntity.ok("Token is valid for user: " + authentication.getName());
    }
}