package com.backendsems.experiments.application.internal.commandservices;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.entities.ExperimentAssignment;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentAssignmentRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentEventRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperimentCommandServiceImplTest {

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentAssignmentRepository assignmentRepository;

    @Mock
    private ExperimentEventRepository eventRepository;

    private ExperimentCommandServiceImpl service(String forcedProdDemo, String forcedProdRecs,
                                                  String forcedTestDemo, String forcedTestRecs) {
        return new ExperimentCommandServiceImpl(
                experimentRepository, assignmentRepository, eventRepository,
                forcedProdDemo, forcedProdRecs, forcedTestDemo, forcedTestRecs);
    }

    @Test
    void handle_withProductionDeploymentEnvAndOverrideConfigured_returnsForcedVariantAndPersistsIt() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-1"))
                .thenReturn(Optional.empty());

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-1", "production"));

        assertEquals("A", variant);
        verify(assignmentRepository).save(any(ExperimentAssignment.class));
        verify(experimentRepository, never()).findById(any());
    }

    @Test
    void handle_withTestDeploymentEnvAndOverrideConfigured_returnsForcedVariant() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("personalized-recommendations", "42"))
                .thenReturn(Optional.empty());

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("personalized-recommendations", "42", "test"));

        assertEquals("treatment", variant);
    }

    @Test
    void handle_withoutDeploymentEnv_fallsBackToRandomBucketing() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-2"))
                .thenReturn(Optional.empty());
        when(experimentRepository.findById("demo-onboarding"))
                .thenReturn(Optional.of(new Experiment("demo-onboarding", "A:50,B:50", true)));

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-2", null));

        assertTrue(variant.equals("A") || variant.equals("B"));
        verify(experimentRepository).findById("demo-onboarding");
    }

    @Test
    void handle_withDeploymentEnvButNoOverrideConfiguredForThatExperiment_fallsBackToRandomBucketing() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("personalized-recommendations", "43"))
                .thenReturn(Optional.empty());
        when(experimentRepository.findById("personalized-recommendations"))
                .thenReturn(Optional.of(new Experiment("personalized-recommendations", "control:50,treatment:50", true)));

        // Simula @Value("...:") sin configurar: llega como string vacío.
        ExperimentCommandServiceImpl service = service("A", "", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("personalized-recommendations", "43", "production"));

        assertTrue(variant.equals("control") || variant.equals("treatment"));
    }

    @Test
    void handle_calledTwiceWithSameSubjectAndForcedVariant_returnsSameVariantWithoutSecondSave() {
        ExperimentAssignment existingAssignment = new ExperimentAssignment("demo-onboarding", "visitor-3", "A");
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-3"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingAssignment));

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String first = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-3", "production"));
        String second = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-3", "production"));

        assertEquals("A", first);
        assertEquals("A", second);
        verify(assignmentRepository, times(1)).save(any(ExperimentAssignment.class));
    }
}
