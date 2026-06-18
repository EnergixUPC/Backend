package com.backendsems.iam.interfaces.rest;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.interfaces.rest.resources.*;
import com.backendsems.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.backendsems.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.backendsems.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.backendsems.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints")
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final HashingService hashingService;

    public AuthenticationController(UserCommandService userCommandService, TokenService tokenService, UserRepository userRepository, HashingService hashingService) {
        this.userCommandService = userCommandService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign-in", description = "Sign-in with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign-up", description = "Sign-up with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")})
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        try {
            var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
            var user = userCommandService.handle(signUpCommand);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
            return new ResponseEntity<>(userResource, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Sign-up error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error during sign-up: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/sign-out")
    @Operation(summary = "Sign-out", description = "Sign-out securely by revoking the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed out successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid token.")})
    public ResponseEntity<?> signOut(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.revokeToken(token);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password for the user with the given email.")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordResource resource) {
        var userOpt = userRepository.findByEmail(resource.email());
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            user.updatePassword(hashingService.encode("password123")); // Default reset password
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password", description = "Change password for the current user.")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordResource resource) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var user = userOpt.get();
        if (!hashingService.matches(resource.oldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect old password");
        }
        user.updatePassword(hashingService.encode(resource.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh authentication token using refresh token.")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenResource resource) {
        String refreshToken = resource.refreshToken();
        if (tokenService.validateToken(refreshToken) && !tokenService.isTokenRevoked(refreshToken)) {
            String email = tokenService.getEmailFromToken(refreshToken);
            String newAccessToken = tokenService.generateToken(email);
            return ResponseEntity.ok(new TokenResponseResource(newAccessToken, refreshToken, 86400, "Bearer"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate the provided access token.")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = tokenService.validateToken(token) && !tokenService.isTokenRevoked(token);
            return ResponseEntity.ok(new ValidateTokenResource(isValid));
        }
        return ResponseEntity.ok(new ValidateTokenResource(false));
    }
}
