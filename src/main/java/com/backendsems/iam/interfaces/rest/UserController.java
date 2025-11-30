package com.backendsems.iam.interfaces.rest;

import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;
import com.backendsems.iam.domain.services.UserQueryService;
import com.backendsems.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - Controlador REST para usuarios
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
public class UserController {

    private final UserQueryService userQueryService;

    public UserController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * Get user by id
     * @param id the user id
     * @return the user
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
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
    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.")})
    public ResponseEntity<?> getAllUsers() {
        // Implementation to get all users
        return ResponseEntity.ok().build();
    }
}