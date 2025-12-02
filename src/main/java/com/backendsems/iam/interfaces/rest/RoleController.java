package com.backendsems.iam.interfaces.rest;

import com.backendsems.iam.domain.services.RoleQueryService;
import com.backendsems.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RoleController - Controlador REST para roles
 */
@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Roles API")
public class RoleController {

    private final RoleQueryService roleQueryService;

    public RoleController(RoleQueryService roleQueryService) {
        this.roleQueryService = roleQueryService;
    }

    /**
     * Get all roles
     * @return list of roles
     */
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        // Implementation to get all roles
        return ResponseEntity.ok().build();
    }

    /**
     * Get role by id
     * @param id the role id
     * @return the role
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        // Implementation to get role by id
        return ResponseEntity.ok().build();
    }
}