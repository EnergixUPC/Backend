package com.backendsems.experiments.interfaces.rest;

import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.commands.RecordExperimentEventCommand;
import com.backendsems.experiments.domain.model.queries.GetExperimentResultsQuery;
import com.backendsems.experiments.domain.services.ExperimentCommandService;
import com.backendsems.experiments.domain.services.ExperimentQueryService;
import com.backendsems.experiments.interfaces.rest.resources.AssignmentRequestResource;
import com.backendsems.experiments.interfaces.rest.resources.RecordEventResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ExperimentsController
 * Núcleo de experimentación (Fase 0 del plan de Capítulo 8): asignación de variantes,
 * registro de eventos de conversión y resultados agregados por experimento.
 * Las rutas de asignación/eventos son públicas porque también las usan visitantes anónimos
 * (Landing Page, modo demo) que todavía no tienen sesión.
 */
@RestController
@RequestMapping(value = "/api/v1/experiments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Experiments", description = "Experiment variant assignment, event tracking and results")
public class ExperimentsController {

    private final ExperimentCommandService commandService;
    private final ExperimentQueryService queryService;

    public ExperimentsController(ExperimentCommandService commandService, ExperimentQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/{key}/assignment")
    @Operation(summary = "Assign (or retrieve) the variant for a subject in an experiment")
    public ResponseEntity<Map<String, String>> assign(@PathVariable String key,
                                                        @RequestBody AssignmentRequestResource request) {
        String variant = commandService.handle(new AssignVariantCommand(key, request.subjectId(), request.deploymentEnv()));
        return ResponseEntity.ok(Map.of("experimentKey", key, "variant", variant));
    }

    @PostMapping("/{key}/events")
    @Operation(summary = "Record an experiment/conversion event for a subject")
    public ResponseEntity<Map<String, Boolean>> recordEvent(@PathVariable String key,
                                                              @RequestBody RecordEventResource request) {
        commandService.handle(new RecordExperimentEventCommand(
                key, request.subjectId(), request.eventName(), request.variant(), request.metadata()));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{key}/results")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get aggregated assignment and event counts per variant")
    public ResponseEntity<Map<String, Object>> results(@PathVariable String key) {
        return ResponseEntity.ok(queryService.handle(new GetExperimentResultsQuery(key)));
    }
}
