package com.backendsems.experiments.application.internal.queryservices;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import com.backendsems.experiments.domain.model.entities.ExperimentEvent;
import com.backendsems.experiments.domain.model.queries.GetExperimentResultsQuery;
import com.backendsems.experiments.domain.services.ExperimentQueryService;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentAssignmentRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentEventRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExperimentQueryServiceImpl implements ExperimentQueryService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentAssignmentRepository assignmentRepository;
    private final ExperimentEventRepository eventRepository;

    public ExperimentQueryServiceImpl(ExperimentRepository experimentRepository,
                                       ExperimentAssignmentRepository assignmentRepository,
                                       ExperimentEventRepository eventRepository) {
        this.experimentRepository = experimentRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Map<String, Object> handle(GetExperimentResultsQuery query) {
        Experiment experiment = experimentRepository.findById(query.experimentKey())
                .orElseThrow(() -> new IllegalArgumentException("Unknown experiment: " + query.experimentKey()));

        var assignments = assignmentRepository.findByExperimentKey(query.experimentKey());
        var events = eventRepository.findByExperimentKey(query.experimentKey());

        List<Map<String, Object>> variantStats = experiment.parseVariants().stream()
                .map(variant -> {
                    long assigned = assignments.stream()
                            .filter(a -> a.getVariant().equals(variant.name()))
                            .count();
                    Map<String, Long> eventCounts = events.stream()
                            .filter(e -> variant.name().equals(e.getVariant()))
                            .collect(Collectors.groupingBy(ExperimentEvent::getEventName, Collectors.counting()));
                    return Map.<String, Object>of(
                            "variant", variant.name(),
                            "weight", variant.weight(),
                            "assigned", assigned,
                            "events", eventCounts
                    );
                })
                .collect(Collectors.toList());

        return Map.of(
                "experimentKey", query.experimentKey(),
                "totalAssigned", (long) assignments.size(),
                "variants", variantStats
        );
    }
}
