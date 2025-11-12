package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.valueobjects.LoginCredentials;
import com.backendsems.SEMS.domain.model.valueobjects.TokenPair;
import com.backendsems.SEMS.domain.model.valueobjects.Email;
import com.backendsems.SEMS.domain.model.valueobjects.UserProfile;
import com.backendsems.SEMS.domain.services.AuthenticationService;
import com.backendsems.SEMS.interfaces.rest.dto.RegisterUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import java.util.Map;
import java.util.HashMap;
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    public AuthenticationController(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCredentials credentials) {
        try {
            System.out.println("========================================");
            System.out.println("LOGIN ATTEMPT");
            System.out.println("Username received: " + credentials.getUsername());
            System.out.println("Password received: " + credentials.getPassword());
            System.out.println("Password length: " + (credentials.getPassword() != null ? credentials.getPassword().length() : "null"));
            System.out.println("========================================");

            TokenPair token = authenticationService.authenticate(credentials);

            System.out.println("LOGIN SUCCESS!");
            System.out.println("Token generated: " + token.getAccessToken());

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("========================================");
            System.out.println("LOGIN FAILED!");
            System.out.println("Error type: " + e.getClass().getName());
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Stack trace:");
            e.printStackTrace();
            System.out.println("========================================");

            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerDto) {
        try {
            System.out.println("========================================");
            System.out.println("REGISTRATION ATTEMPT");
            System.out.println("Email: " + registerDto.getEmail());
            System.out.println("Name: " + registerDto.getFirstName() + " " + registerDto.getLastName());
            System.out.println("========================================");

            // Verificar si el usuario ya existe
            if (userRepository.findByUsername(registerDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User with this email already exists"));
            }

            // Crear el User
            User user = User.builder()
                    .username(registerDto.getEmail())
                    .email(registerDto.getEmail())
                    .password(registerDto.getPassword())
                    .firstName(registerDto.getFirstName())
                    .lastName(registerDto.getLastName())
                    .phoneNumber(registerDto.getPhoneNumber())
                    .address(registerDto.getAddress())
                    .role(User.Role.USER)
                    .build();

            // Registrar el usuario
            User registeredUser = authenticationService.register(user);

            System.out.println("REGISTRATION SUCCESS!");
            System.out.println("User ID: " + registeredUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                    "id", registeredUser.getId(),
                    "email", registeredUser.getEmail(),
                    "firstName", registeredUser.getFirstName(),
                    "lastName", registeredUser.getLastName(),
                    "phoneNumber", registeredUser.getPhoneNumber() != null ? registeredUser.getPhoneNumber() : "",
                    "address", registeredUser.getAddress() != null ? registeredUser.getAddress() : ""
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("========================================");
            System.out.println("REGISTRATION FAILED!");
            System.out.println("Error type: " + e.getClass().getName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        try {
            User user = authenticationService.getCurrentUser(authentication.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(Authentication authentication) {
        return ResponseEntity.ok("Token is valid for user: " + authentication.getName());
    }
    @GetMapping("/generate-password/{password}")
    public ResponseEntity<String> generatePassword(@PathVariable String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        return ResponseEntity.ok(encodedPassword);
    }
    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        try {
            System.out.println("========================================");
            System.out.println("TEST PASSWORD");
            System.out.println("Username: " + username);
            System.out.println("Password received: " + password);

            // Buscar el usuario
            User user = userRepository.findByEmailOrUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User found: " + user.getEmail());
            System.out.println("Password in DB: " + user.getPassword());

            // Verificar la contraseña
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, user.getPassword());

            System.out.println("Password matches: " + matches);
            System.out.println("========================================");

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("passwordMatches", matches);
            response.put("passwordInDB", user.getPassword());
            response.put("passwordReceived", password);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/generate-hash/{password}")
    public ResponseEntity<Map<String, String>> generateHash(@PathVariable String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);

        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);

        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);

        return ResponseEntity.ok(response);
    }
}