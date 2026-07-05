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

    public ExperimentCommandServiceImpl(ExperimentRepository experimentRepository,
                                         ExperimentAssignmentRepository assignmentRepository,
                                         ExperimentEventRepository eventRepository) {
        this.experimentRepository = experimentRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public String handle(AssignVariantCommand command) {
        var existing = assignmentRepository.findByExperimentKeyAndSubjectId(command.experimentKey(), command.subjectId());
        if (existing.isPresent()) {
            return existing.get().getVariant();
        }

        Experiment experiment = experimentRepository.findById(command.experimentKey())
                .orElseThrow(() -> new IllegalArgumentException("Unknown experiment: " + command.experimentKey()));

        String variant = pickVariant(experiment, command.subjectId());
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
