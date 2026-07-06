package com.backendsems.experiments.application.internal.commandservices;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.commands.RecordExperimentEventCommand;
import com.backendsems.experiments.domain.model.commands.SeedExperimentsCommand;
import com.backendsems.experiments.domain.model.entities.ExperimentAssignment;
import com.backendsems.experiments.domain.model.entities.ExperimentEvent;
import com.backendsems.experiments.domain.services.ExperimentCommandService;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentAssignmentRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentEventRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ExperimentCommandServiceImpl
 * Bucketing determinístico e idempotente de sujetos a variantes, y registro de eventos.
 */
@Service
public class ExperimentCommandServiceImpl implements ExperimentCommandService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentAssignmentRepository assignmentRepository;
    private final ExperimentEventRepository eventRepository;

    private final String forcedProductionDemoOnboarding;
    private final String forcedProductionRecommendations;
    private final String forcedTestDemoOnboarding;
    private final String forcedTestRecommendations;

    public ExperimentCommandServiceImpl(
            ExperimentRepository experimentRepository,
            ExperimentAssignmentRepository assignmentRepository,
            ExperimentEventRepository eventRepository,
            @Value("${experiments.force-variant.production.demo-onboarding:}") String forcedProductionDemoOnboarding,
            @Value("${experiments.force-variant.production.personalized-recommendations:}") String forcedProductionRecommendations,
            @Value("${experiments.force-variant.test.demo-onboarding:}") String forcedTestDemoOnboarding,
            @Value("${experiments.force-variant.test.personalized-recommendations:}") String forcedTestRecommendations) {
        this.experimentRepository = experimentRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventRepository = eventRepository;
        this.forcedProductionDemoOnboarding = blankToNull(forcedProductionDemoOnboarding);
        this.forcedProductionRecommendations = blankToNull(forcedProductionRecommendations);
        this.forcedTestDemoOnboarding = blankToNull(forcedTestDemoOnboarding);
        this.forcedTestRecommendations = blankToNull(forcedTestRecommendations);
    }

    @Override
    public String handle(AssignVariantCommand command) {
        var existing = assignmentRepository.findByExperimentKeyAndSubjectId(command.experimentKey(), command.subjectId());
        if (existing.isPresent()) {
            return existing.get().getVariant();
        }

        String forcedVariant = resolveForcedVariant(command.deploymentEnv(), command.experimentKey());
        String variant = forcedVariant != null
                ? forcedVariant
                : pickVariant(mustFindExperiment(command.experimentKey()), command.subjectId());

        assignmentRepository.save(new ExperimentAssignment(command.experimentKey(), command.subjectId(), variant));
        return variant;
    }

    @Override
    public void handle(RecordExperimentEventCommand command) {
        String variant = command.variant();
        if (variant == null || variant.isBlank()) {
            variant = assignmentRepository
                    .findByExperimentKeyAndSubjectId(command.experimentKey(), command.subjectId())
                    .map(ExperimentAssignment::getVariant)
                    .orElse(null);
        }
        eventRepository.save(new ExperimentEvent(
                command.experimentKey(), command.subjectId(), variant, command.eventName(), command.metadata()));
    }

    @Override
    public void handle(SeedExperimentsCommand command) {
        seedIfMissing("demo-onboarding", "A:50,B:50");
        seedIfMissing("personalized-recommendations", "control:50,treatment:50");
        seedIfMissing("demo-conversion", "default:100");
    }

    private void seedIfMissing(String key, String variantsCsv) {
        if (experimentRepository.existsById(key)) return;
        experimentRepository.save(new Experiment(key, variantsCsv, true));
    }

    private Experiment mustFindExperiment(String experimentKey) {
        return experimentRepository.findById(experimentKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown experiment: " + experimentKey));
    }

    /**
     * Devuelve la variante forzada para (deploymentEnv, experimentKey) según
     * experiments.force-variant.*, o {@code null} si no aplica (deploymentEnv ausente, o sin
     * override configurado para ese par) — en cuyo caso se usa el bucketing aleatorio.
     */
    private String resolveForcedVariant(String deploymentEnv, String experimentKey) {
        if ("production".equals(deploymentEnv)) {
            if ("demo-onboarding".equals(experimentKey)) return forcedProductionDemoOnboarding;
            if ("personalized-recommendations".equals(experimentKey)) return forcedProductionRecommendations;
        } else if ("test".equals(deploymentEnv)) {
            if ("demo-onboarding".equals(experimentKey)) return forcedTestDemoOnboarding;
            if ("personalized-recommendations".equals(experimentKey)) return forcedTestRecommendations;
        }
        return null;
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    /** Hash estable de subjectId+experimentKey en [0,100) contra el peso acumulado de cada variante. */
    private String pickVariant(Experiment experiment, String subjectId) {
        var variants = experiment.parseVariants();
        int bucket = Math.floorMod((subjectId + "::" + experiment.getKey()).hashCode(), 100);
        int cumulative = 0;
        for (var variant : variants) {
            cumulative += variant.weight();
            if (bucket < cumulative) {
                return variant.name();
            }
        }
        return variants.get(variants.size() - 1).name();
    }
}
