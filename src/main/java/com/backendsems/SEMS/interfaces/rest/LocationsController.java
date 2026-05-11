package com.backendsems.SEMS.interfaces.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.backendsems.SEMS.domain.model.queries.GetLocationsByUserIdQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.LocationCommandService;
import com.backendsems.SEMS.domain.services.LocationQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.CreateLocationResource;
import com.backendsems.SEMS.interfaces.rest.resources.LocationResource;
import com.backendsems.SEMS.interfaces.rest.transform.CreateLocationFromResourceAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.LocationFromEntityAssembler;
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
 * LocationsController
 * Controlador REST para gestionar ubicaciones.
 */
@RestController
@RequestMapping(value = "/api/v1/locations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Locations", description = "Locations Endpoints")
public class LocationsController {

    private final LocationCommandService locationCommandService;
    private final LocationQueryService locationQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    public LocationsController(
        LocationCommandService locationCommandService,
        LocationQueryService locationQueryService,
        TokenService tokenService,
        ProfilesContextFacade profilesContextFacade
    ) {
        this.locationCommandService = locationCommandService;
        this.locationQueryService = locationQueryService;
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
    @Operation(summary = "Create a new location", description = "Create a new location for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Location created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<LocationResource> createLocation(
        @RequestBody CreateLocationResource resource,
        @RequestHeader("Authorization") String authHeader
    ) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();
        if (resource == null || resource.name() == null || resource.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        var command = CreateLocationFromResourceAssembler.toCommand(resource);
        Long locationId;
        try {
            locationId = locationCommandService.handle(command, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (locationId == null || locationId == 0L) {
            return ResponseEntity.badRequest().build();
        }

        var locationResource = new LocationResource(
            locationId,
            resource.name(),
            String.valueOf(userId.id())
        );
        return new ResponseEntity<>(locationResource, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get locations", description = "Get locations for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Locations found")
    })
    public ResponseEntity<List<LocationResource>> getLocations(
        @RequestHeader("Authorization") String authHeader
    ) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        var locations = locationQueryService.handle(new GetLocationsByUserIdQuery(userId));
        var resources = locations.stream()
            .map(LocationFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
