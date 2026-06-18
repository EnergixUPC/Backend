package com.backendsems.iam.interfaces.rest;

import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;
import com.backendsems.iam.domain.model.queries.GetUserByEmailQuery;
import com.backendsems.iam.domain.model.commands.UpdateUserPlanCommand;
import com.backendsems.iam.domain.services.UserQueryService;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.interfaces.rest.resources.UpdateUserPlanResource;
import com.backendsems.iam.interfaces.rest.resources.ExistsResource;
import com.backendsems.iam.interfaces.rest.resources.ChangePasswordResource;
import com.backendsems.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backendsems.iam.interfaces.rest.resources.UserResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - Controlador REST para usuarios
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserRepository userRepository;
    private final HashingService hashingService;

    public UserController(UserQueryService userQueryService, UserCommandService userCommandService, UserRepository userRepository, HashingService hashingService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    /**
     * Get user by id
     * @param id the user id
     * @return the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            var getUserByIdQuery = new GetUserByIdQuery(id);
            var user = userQueryService.handle(getUserByIdQuery);
            if (user.isPresent()) {
                var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
                return ResponseEntity.ok(userResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all users
     * @return list of users
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResource.class))))})
    public ResponseEntity<?> getAllUsers() {
        var users = userQueryService.handleGetAll();
        var userResources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(userResources);
    }

    /**
     * Update user plan
     * @param id the user id
     * @param resource the update user plan resource
     * @return the updated user
     */
    @PutMapping("/{id}/plan")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user plan", description = "Updates the plan of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User plan updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<?> updateUserPlan(@PathVariable Long id, @RequestBody UpdateUserPlanResource resource) {
        try {
            var command = new UpdateUserPlanCommand(id, resource.plan());
            var user = userCommandService.handle(command);
            if (user.isPresent()) {
                var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
                return ResponseEntity.ok(userResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email.")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        var query = new GetUserByEmailQuery(email);
        var user = userQueryService.handle(query);
        if (user.isPresent()) {
            return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by username", description = "Retrieve a user by their username (email).")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        var query = new GetUserByEmailQuery(username);
        var user = userQueryService.handle(query);
        if (user.isPresent()) {
            return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}/exists")
    @Operation(summary = "Check if email exists", description = "Check if email is already registered.")
    public ResponseEntity<?> existsByEmail(@PathVariable String email) {
        var query = new GetUserByEmailQuery(email);
        var user = userQueryService.handle(query);
        return ResponseEntity.ok(new ExistsResource(user.isPresent()));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password", description = "Change password for user by ID.")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordResource resource) {
        var userOpt = userRepository.findById(id);
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
}