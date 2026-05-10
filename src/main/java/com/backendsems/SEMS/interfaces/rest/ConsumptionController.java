package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetAllConsumptionsQuery;
import com.backendsems.SEMS.domain.model.queries.GetConsumptionsByDeviceIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetDevicesByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByDeviceIdsQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.ConsumptionCommandService;
import com.backendsems.SEMS.domain.services.ConsumptionQueryService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.AddConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.ConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.UserWeeklyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.transform.AddConsumptionFromResourceAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.ConsumptionFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.UserWeeklyConsumptionAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/consumptions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Consumptions", description = "Available Consumption Endpoints for Mobile App")
public class ConsumptionController {

    private final ConsumptionCommandService consumptionCommandService;
    private final ConsumptionQueryService consumptionQueryService;
    private final DeviceQueryService deviceQueryService;

    public ConsumptionController(ConsumptionCommandService consumptionCommandService,
                                 ConsumptionQueryService consumptionQueryService,
                                 DeviceQueryService deviceQueryService) {
        this.consumptionCommandService = consumptionCommandService;
        this.consumptionQueryService = consumptionQueryService;
        this.deviceQueryService = deviceQueryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new consumption record", description = "Create a new consumption record from mobile app")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consumption record created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ConsumptionResource> createConsumption(
        @RequestBody AddConsumptionResource resource) {
        try {
            var command = AddConsumptionFromResourceAssembler.toCommand(resource);
            var consumptionId = consumptionCommandService.handle(command);

            if (consumptionId == null || consumptionId == 0L) {
                return ResponseEntity.badRequest().build();
            }

            var consumptions = consumptionQueryService.handle(
                new GetConsumptionsByDeviceIdQuery(resource.deviceId())
            );

            var createdConsumption = consumptions.stream()
                .filter(c -> c.getId().equals(consumptionId))
                .findFirst()
                .orElse(null);

            if (createdConsumption == null) {
                return ResponseEntity.notFound().build();
            }

            var consumptionResource = ConsumptionFromEntityAssembler.toResource(createdConsumption);
            return new ResponseEntity<>(consumptionResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/devices/{deviceId}")
    @Operation(summary = "Get consumptions by device id", description = "Get all consumption records for a specific device")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumptions found"),
        @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<List<ConsumptionResource>> getConsumptionsByDeviceId(
        @PathVariable String deviceId) {
        var consumptions = consumptionQueryService.handle(new GetConsumptionsByDeviceIdQuery(deviceId));

        if (consumptions.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        var consumptionResources = consumptions.stream()
            .map(ConsumptionFromEntityAssembler::toResource)
            .collect(Collectors.toList());

        return ResponseEntity.ok(consumptionResources);
    }

    @GetMapping
    @Operation(summary = "Get all consumptions", description = "Get all consumption records")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumptions found")
    })
    public ResponseEntity<List<ConsumptionResource>> getAllConsumptions() {
        var consumptions = consumptionQueryService.handle(new GetAllConsumptionsQuery());
        var consumptionResources = consumptions.stream()
            .map(ConsumptionFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(consumptionResources);
    }

    @GetMapping("/weekly/users/{userId}")
    @Operation(
        summary = "Get weekly consumption by user",
        description = "Returns daily consumption (kWh) for each day of the current week (Mon-Sun) " +
                      "and the total kWh per device. Raw values are stored as kW·min and converted to kWh."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Weekly consumption data returned"),
        @ApiResponse(responseCode = "404", description = "User has no devices")
    })
    public ResponseEntity<UserWeeklyConsumptionResource> getWeeklyConsumptionByUser(
            @PathVariable Long userId) {

        var devices = deviceQueryService.handle(new GetDevicesByUserIdQuery(new UserId(userId)));
        if (devices.isEmpty()) return ResponseEntity.notFound().build();

        List<String> deviceIds = devices.stream()
                .map(d -> String.valueOf(d.getId()))
                .collect(Collectors.toList());

        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = weekStart.plusDays(6);

        var consumptions = consumptionQueryService.handle(new GetWeeklyConsumptionByDeviceIdsQuery(
                deviceIds,
                weekStart.atStartOfDay(),
                weekEnd.atTime(LocalTime.MAX)
        ));

        var resource = UserWeeklyConsumptionAssembler.toResource(consumptions, devices, weekStart, weekEnd);
        return ResponseEntity.ok(resource);
    }
}

