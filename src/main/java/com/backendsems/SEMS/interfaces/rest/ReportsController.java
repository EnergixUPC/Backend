package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetTopDevicesByUserQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.TopDeviceResource;
import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.transform.TopDeviceResourceFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.WeeklyConsumptionResourceFromEntityAssembler;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportsController
 * Controlador REST para gestionar reportes de consumo.
 */
@RestController
@RequestMapping(value = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Available Report Endpoints")
public class ReportsController {

    private final DeviceQueryService deviceQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    /**
     * Constructor
     * @param deviceQueryService Servicio de queries para dispositivos
     * @param tokenService Servicio de tokens
     * @param profilesContextFacade Fachada de perfiles
     */
    public ReportsController(DeviceQueryService deviceQueryService, 
                           TokenService tokenService, 
                           ProfilesContextFacade profilesContextFacade) {
        this.deviceQueryService = deviceQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Obtener el top 3 dispositivos que más consumen del usuario actual
     * @param authHeader Header de autorización
     * @return Lista de dispositivos con mayor consumo
     */
    @GetMapping("/top-devices")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get top 3 consuming devices", description = "Get the top 3 devices with highest consumption for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top devices retrieved successfully")})
    public ResponseEntity<List<TopDeviceResource>> getTopDevices(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            
            if (profileId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            var userId = new UserId(profileId);
            var query = new GetTopDevicesByUserQuery(userId, 3);
            var topDevices = deviceQueryService.handle(query);
            
            var topDeviceResources = topDevices.stream()
                    .map(TopDeviceResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(topDeviceResources);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener el consumo semanal del usuario actual
     * @param authHeader Header de autorización
     * @return Datos de consumo semanal
     */
    @GetMapping("/weekly-consumption")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get weekly consumption", description = "Get weekly consumption data for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly consumption data retrieved successfully")})
    public ResponseEntity<WeeklyConsumptionResource> getWeeklyConsumption(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            
            if (profileId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            var userId = new UserId(profileId);
            var query = new GetWeeklyConsumptionByUserQuery(userId);
            var dailySummaryData = deviceQueryService.handleDailySummary(query);
            
            var weeklyConsumptionResource = WeeklyConsumptionResourceFromEntityAssembler
                    .toResourceFromDailySummary(dailySummaryData);
                    
            return ResponseEntity.ok(weeklyConsumptionResource);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}