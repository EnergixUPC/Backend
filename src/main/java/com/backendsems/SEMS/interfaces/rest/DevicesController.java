package com.backendsems.SEMS.interfaces.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.backendsems.SEMS.domain.model.commands.DeleteDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.UpdateDeviceCommand;
import com.backendsems.SEMS.domain.model.queries.*;
import com.backendsems.SEMS.domain.services.DeviceCommandService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.*;
import com.backendsems.SEMS.interfaces.rest.resources.UpdateDeviceResource;
import com.backendsems.SEMS.interfaces.rest.transform.*;
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
 * DevicesController
 * Controlador REST para gestionar dispositivos.
 */
@RestController
@RequestMapping(value = "/api/v1/devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Devices", description = "Available Device Endpoints")
public class DevicesController {

    private final DeviceCommandService deviceCommandService;
    private final DeviceQueryService deviceQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    /**
     * Constructor
     * @param deviceCommandService Servicio de comandos para dispositivos
     * @param deviceQueryService Servicio de queries para dispositivos
     * @param tokenService Servicio de tokens
     * @param profilesContextFacade Fachada de perfiles
     */
    public DevicesController(
        DeviceCommandService deviceCommandService,
        DeviceQueryService deviceQueryService,
        TokenService tokenService,
        ProfilesContextFacade profilesContextFacade
    ) {
        this.deviceCommandService = deviceCommandService;
        this.deviceQueryService = deviceQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Crear un nuevo dispositivo
     * @param resource Recurso de creación
     * @param authHeader Header de autorización
     * @return Recurso del dispositivo creado
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Create a new device",
        description = "Create a new device"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Device created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
        }
    )
    public ResponseEntity<DeviceResource> createDevice(
        @RequestBody CreateDeviceResource resource,
        @RequestHeader("Authorization") String authHeader
    ) {
        // Extraer token del header
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return ResponseEntity.badRequest().build();
        var userId = new com.backendsems.SEMS.domain.model.valueobjects.UserId(
            profileId
        );

        var command = CreateDeviceFromResourceAssembler.toCommand(resource);
        var deviceId = deviceCommandService.handle(command, userId);
        if (
            deviceId == null || deviceId == 0L
        ) return ResponseEntity.badRequest().build();
        var device = deviceQueryService.handle(
            new GetDeviceByIdQuery(deviceId)
        );
        if (device == null) return ResponseEntity.notFound().build();
        var deviceResource = DeviceFromEntityAssembler.toResource(device);
        return new ResponseEntity<>(deviceResource, HttpStatus.CREATED);
    }

    /**
     * Obtener todos los dispositivos
     * @return Lista de recursos de dispositivos
     */
    @GetMapping
    @Operation(summary = "Get all devices", description = "Get all devices")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Devices found"),
        }
    )
    public ResponseEntity<List<DeviceResource>> getAllDevices(
        @RequestHeader("Authorization") String authHeader
    ) {
        // Extraer token del header
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return ResponseEntity.badRequest().build();
        var userId = new com.backendsems.SEMS.domain.model.valueobjects.UserId(
            profileId
        );

        var devices = deviceQueryService.handle(new GetAllDevicesQuery(userId));
        var deviceResources = devices
            .stream()
            .map(DeviceFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(deviceResources);
    }

    /**
     * Obtener dispositivo por ID
     * @param deviceId ID del dispositivo
     * @return Recurso del dispositivo
     */
    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device by id", description = "Get device by id")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(
                responseCode = "404",
                description = "Device not found"
            ),
        }
    )
    public ResponseEntity<DeviceResource> getDeviceById(
        @PathVariable Long deviceId
    ) {
        var device = deviceQueryService.handle(
            new GetDeviceByIdQuery(deviceId)
        );
        if (device == null) return ResponseEntity.notFound().build();
        var deviceResource = DeviceFromEntityAssembler.toResource(device);
        return ResponseEntity.ok(deviceResource);
    }

    /**
     * Obtener dispositivos por userId
     * @param userId ID del usuario
     * @return Lista de recursos de dispositivos
     */
    @GetMapping("/users/{userId}")
    @Operation(
        summary = "Get devices by user id",
        description = "Get devices by user id"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Devices found"),
        }
    )
    public ResponseEntity<List<DeviceResource>> getDevicesByUserId(
        @PathVariable String userId
    ) {
        // Assuming userId is String, need to convert to UserId
        // For simplicity, assume query takes String
        // But in domain, UserId
        // Perhaps adjust
        var devices = deviceQueryService.handle(
            new GetDevicesByUserIdQuery(
                new com.backendsems.SEMS.domain.model.valueobjects.UserId(
                    Long.parseLong(userId)
                )
            )
        );
        var deviceResources = devices
            .stream()
            .map(DeviceFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(deviceResources);
    }

    /**
     * Obtener consumo de un dispositivo
     * @param deviceId ID del dispositivo
     * @return Lista de recursos de consumo
     */
    @GetMapping("/{deviceId}/consumption")
    @Operation(
        summary = "Get device consumption",
        description = "Get device consumption"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Consumption found"
            ),
        }
    )
    public ResponseEntity<List<DeviceConsumptionResource>> getDeviceConsumption(
        @PathVariable Long deviceId
    ) {
        var consumptions = deviceQueryService.handle(
            new GetDeviceConsumptionByDeviceIdQuery(deviceId)
        );
        var consumptionResources = consumptions
            .stream()
            .map(DeviceConsumptionFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(consumptionResources);
    }

    /**
     * Actualizar atributos de un dispositivo
     * @param deviceId ID del dispositivo
     * @param resource Campos a actualizar (todos opcionales)
     * @return Recurso del dispositivo actualizado
     */
    @PatchMapping("/{deviceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Update device",
        description = "Update device attributes (partial update)"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Device updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(
                responseCode = "404",
                description = "Device not found"
            ),
        }
    )
    public ResponseEntity<DeviceResource> updateDevice(
        @PathVariable Long deviceId,
        @RequestBody UpdateDeviceResource resource
    ) {
        var command = UpdateDeviceFromResourceAssembler.toCommand(
            deviceId,
            resource
        );
        var device = deviceCommandService.handle(command);
        return ResponseEntity.ok(DeviceFromEntityAssembler.toResource(device));
    }

    /**
     * Eliminar dispositivo
     * @param deviceId ID del dispositivo
     * @return Mensaje de eliminación
     */
    @DeleteMapping("/{deviceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete device", description = "Delete device")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Device deleted"),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - Device doesn't belong to user"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Device not found"
            ),
        }
    )
    public ResponseEntity<String> deleteDevice(@PathVariable Long deviceId) {
        // Eliminar el dispositivo sin validaciones adicionales por ahora
        var command = new DeleteDeviceCommand(deviceId);
        deviceCommandService.handle(command);
        return ResponseEntity.ok("Device with given id successfully deleted");
    }
}
