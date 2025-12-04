package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetPreferencesByUserIdAndDeviceIdQuery;
import com.backendsems.SEMS.domain.services.DeviceCommandService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.PreferencesResource;
import com.backendsems.SEMS.interfaces.rest.resources.UpdatePreferencesResource;
import com.backendsems.SEMS.interfaces.rest.transform.PreferencesFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.UpdatePreferencesFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * PreferencesController
 * Controlador REST para gestionar preferencias de usuario (globales para todos los dispositivos).
 */
@RestController
@RequestMapping(value = "/api/v1/users/{userId}/preferences", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Preferences", description = "User preferences management endpoints")
public class PreferencesController {

    private final DeviceCommandService deviceCommandService;
    private final DeviceQueryService deviceQueryService;

    public PreferencesController(DeviceCommandService deviceCommandService, DeviceQueryService deviceQueryService) {
        this.deviceCommandService = deviceCommandService;
        this.deviceQueryService = deviceQueryService;
    }

    /**
     * Obtener preferencias del usuario
     * @param userId ID del usuario
     * @return Recurso de preferencias
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user preferences", description = "Get global preferences for all user devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences found"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<PreferencesResource> getPreferences(@PathVariable Long userId) {
        var preferences = deviceQueryService.handle(new GetPreferencesByUserIdAndDeviceIdQuery(userId, null));
        
        // Si no existen preferencias, retornar valores por defecto
        if (preferences == null) {
            return ResponseEntity.ok(new PreferencesResource(
                    null,  // id
                    String.valueOf(userId), // userId como String
                    null,  // deviceId (null para preferencias globales)
                    100.0, // threshold por defecto
                    true,  // notificationEnabled por defecto
                    true,  // habilitarMonitoreoEnergia
                    true,  // recibirAlertasAltoConsumo
                    true,  // monitorearCalefaccionRefrigeracion
                    true,  // monitorearElectrodomesticosPrincipales
                    true,  // monitorearElectronicos
                    true,  // monitorearDispositivosCocina
                    true,  // incluirIluminacionExterior
                    true,  // rastrearEnergiaEspera
                    true,  // emailsResumenDiario
                    true,  // reportesProgresoSemanal
                    true,  // sugerirAutomizacionesAhorro
                    true   // alertasDispositivosDesconectados
            ));
        }
        
        var preferencesResource = PreferencesFromEntityAssembler.toResource(preferences);
        return ResponseEntity.ok(preferencesResource);
    }

    /**
     * Actualizar preferencias del usuario
     * @param userId ID del usuario
     * @param resource Recurso de actualización de preferencias
     * @return Recurso de preferencias actualizado
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user preferences", description = "Update global preferences for all user devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences updated"),
            @ApiResponse(responseCode = "404", description = "Preferences not found")})
    public ResponseEntity<PreferencesResource> updatePreferences(@PathVariable Long userId, @RequestBody UpdatePreferencesResource resource) {
        var command = UpdatePreferencesFromResourceAssembler.toCommand(resource, userId);
        deviceCommandService.handle(command);
        var preferences = deviceQueryService.handle(new GetPreferencesByUserIdAndDeviceIdQuery(userId, null));
        if (preferences == null) return ResponseEntity.notFound().build();
        var preferencesResource = PreferencesFromEntityAssembler.toResource(preferences);
        return ResponseEntity.ok(preferencesResource);
    }
}
