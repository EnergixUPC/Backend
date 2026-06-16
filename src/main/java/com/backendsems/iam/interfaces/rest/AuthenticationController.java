package com.backendsems.iam.interfaces.rest;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.backendsems.iam.interfaces.rest.resources.SignInResource;
import com.backendsems.iam.interfaces.rest.resources.SignUpResource;
import com.backendsems.iam.interfaces.rest.resources.UserResource;
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

    public AuthenticationController(UserCommandService userCommandService, TokenService tokenService) {
        this.userCommandService = userCommandService;
        this.tokenService = tokenService;
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
}
