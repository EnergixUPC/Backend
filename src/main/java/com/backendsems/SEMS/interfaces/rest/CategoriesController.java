package com.backendsems.SEMS.interfaces.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.backendsems.SEMS.domain.model.queries.GetCategoriesByUserIdQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.CategoryCommandService;
import com.backendsems.SEMS.domain.services.CategoryQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.CategoryResource;
import com.backendsems.SEMS.interfaces.rest.resources.CreateCategoryResource;
import com.backendsems.SEMS.interfaces.rest.transform.CategoryFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.CreateCategoryFromResourceAssembler;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * CategoriesController
 * Controlador REST para gestionar categorias.
 */
@RestController
@RequestMapping(value = "/api/v1/categories", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Categories", description = "Categories Endpoints")
public class CategoriesController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    public CategoriesController(
        CategoryCommandService categoryCommandService,
        CategoryQueryService categoryQueryService,
        TokenService tokenService,
        ProfilesContextFacade profilesContextFacade
    ) {
        this.categoryCommandService = categoryCommandService;
        this.categoryQueryService = categoryQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    private UserId getUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return null;
        return new UserId(profileId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new category", description = "Create a new category for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CategoryResource> createCategory(
        @RequestBody CreateCategoryResource resource,
        @RequestHeader("Authorization") String authHeader
    ) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();
        if (resource == null || resource.name() == null || resource.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        var command = CreateCategoryFromResourceAssembler.toCommand(resource);
        Long categoryId;
        try {
            categoryId = categoryCommandService.handle(command, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (categoryId == null || categoryId == 0L) {
            return ResponseEntity.badRequest().build();
        }

        var categoryResource = new CategoryResource(
            categoryId,
            resource.name(),
            String.valueOf(userId.id())
        );
        return new ResponseEntity<>(categoryResource, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get categories", description = "Get categories for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories found")
    })
    public ResponseEntity<List<CategoryResource>> getCategories(
        @RequestHeader("Authorization") String authHeader
    ) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        var categories = categoryQueryService.handle(new GetCategoriesByUserIdQuery(userId));
        var resources = categories.stream()
            .map(CategoryFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
