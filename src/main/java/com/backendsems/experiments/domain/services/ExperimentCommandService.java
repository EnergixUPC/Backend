package com.backendsems.experiments.domain.services;

import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.commands.RecordExperimentEventCommand;
import com.backendsems.experiments.domain.model.commands.SeedExperimentsCommand;

public interface ExperimentCommandService {
    String handle(AssignVariantCommand command);

    void handle(RecordExperimentEventCommand command);

    void handle(SeedExperimentsCommand command);
}
