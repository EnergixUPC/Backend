package com.backendsems.experiments.application.internal.eventhandlers;

import com.backendsems.experiments.domain.model.commands.SeedExperimentsCommand;
import com.backendsems.experiments.domain.services.ExperimentCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * ExperimentSeedEventHandler
 * Siembra los experimentos por defecto (Q1 demo-onboarding, Q3 personalized-recommendations,
 * Q6 demo-conversion) al arrancar la aplicación, si aún no existen.
 */
@Service
public class ExperimentSeedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentSeedEventHandler.class);

    private final ExperimentCommandService experimentCommandService;

    public ExperimentSeedEventHandler(ExperimentCommandService experimentCommandService) {
        this.experimentCommandService = experimentCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        LOGGER.info("Verifying default experiments seeding");
        experimentCommandService.handle(new SeedExperimentsCommand());
    }
}
