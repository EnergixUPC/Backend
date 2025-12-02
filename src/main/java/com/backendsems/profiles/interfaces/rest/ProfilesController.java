package com.backendsems.profiles.interfaces.rest;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.domain.services.ProfileQueryService;
import com.backendsems.profiles.interfaces.rest.resources.ProfileResource;
import com.backendsems.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.backendsems.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.backendsems.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * ProfilesController
 * REST controller for managing user profiles.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "User Profile Management Endpoints")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final TokenService tokenService;

    /**
     * Constructor
     * @param profileCommandService Profile command service
     * @param profileQueryService Profile query service
     * @param tokenService Token service
     */
    public ProfilesController(ProfileCommandService profileCommandService, 
                             ProfileQueryService profileQueryService,
                             TokenService tokenService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.tokenService = tokenService;
    }

    /**
     * Get current user profile
     * @param authHeader Authorization header
     * @return Profile resource
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getCurrentProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        
        var profile = profileQueryService.handle(new GetProfileByEmailQuery(email));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Get profile by ID
     * @param profileId Profile ID
     * @return Profile resource
     */
    @GetMapping("/{profileId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get profile by ID", description = "Get a user profile by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Update current user profile
     * @param resource Update profile resource
     * @param authHeader Authorization header
     * @return Updated profile resource
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> updateCurrentProfile(
            @RequestBody UpdateProfileResource resource,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        
        var profile = profileQueryService.handle(new GetProfileByEmailQuery(email));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Create command with the profile ID
        var updateResource = new UpdateProfileResource(
                profile.get().getId(),
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.phone(),
                resource.address(),
                resource.profilePhotoUrl()
        );
        
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(updateResource);
        profileCommandService.handle(command);
        
        var updatedProfile = profileQueryService.handle(new GetProfileByIdQuery(profile.get().getId()));
        if (updatedProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Update profile by ID
     * @param profileId Profile ID
     * @param resource Update profile resource
     * @return Updated profile resource
     */
    @PutMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update profile by ID", description = "Update a user profile by its ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> updateProfileById(
            @PathVariable Long profileId,
            @RequestBody UpdateProfileResource resource) {
        
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Create command with the profile ID
        var updateResource = new UpdateProfileResource(
                profileId,
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.phone(),
                resource.address(),
                resource.profilePhotoUrl()
        );
        
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(updateResource);
        profileCommandService.handle(command);
        
        var updatedProfile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (updatedProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());
        return ResponseEntity.ok(profileResource);
    }
}
