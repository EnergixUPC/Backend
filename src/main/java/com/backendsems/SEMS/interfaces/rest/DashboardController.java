package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DashboardQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.DashboardResource;
import com.backendsems.SEMS.interfaces.rest.transform.DashboardResourceAssembler;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Available Dashboard Endpoints")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    public DashboardController(DashboardQueryService dashboardQueryService,
                               TokenService tokenService,
                               ProfilesContextFacade profilesContextFacade) {
        this.dashboardQueryService = dashboardQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResource> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return ResponseEntity.badRequest().build();

        var data = dashboardQueryService.handle(new GetDashboardByUserIdQuery(new UserId(profileId)));
        return ResponseEntity.ok(DashboardResourceAssembler.toResource(data));
    }
}
