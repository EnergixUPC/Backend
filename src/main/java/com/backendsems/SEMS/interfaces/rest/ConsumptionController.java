package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetAllConsumptionsQuery;
import com.backendsems.SEMS.domain.model.queries.GetConsumptionsByDeviceIdQuery;
import com.backendsems.SEMS.domain.services.ConsumptionCommandService;
import com.backendsems.SEMS.domain.services.ConsumptionQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.AddConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.ConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.transform.AddConsumptionFromResourceAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.ConsumptionFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/consumptions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Consumptions", description = "Available Consumption Endpoints for Mobile App")
public class ConsumptionController {

    private final ConsumptionCommandService consumptionCommandService;
    private final ConsumptionQueryService consumptionQueryService;

    public ConsumptionController(ConsumptionCommandService consumptionCommandService,
                                 ConsumptionQueryService consumptionQueryService) {
        this.consumptionCommandService = consumptionCommandService;
        this.consumptionQueryService = consumptionQueryService;
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
}

