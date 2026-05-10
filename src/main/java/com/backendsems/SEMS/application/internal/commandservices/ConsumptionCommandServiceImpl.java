package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.commands.AddConsumptionCommand;
import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.entities.ConsumptionStatus;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.services.ConsumptionCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConsumptionCommandServiceImpl implements ConsumptionCommandService {

    private static final double HIGH_CONSUMPTION_THRESHOLD_KW_MIN = 5.0;

    private final ConsumptionRepository consumptionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConsumptionCommandServiceImpl(ConsumptionRepository consumptionRepository,
                                         ApplicationEventPublisher eventPublisher) {
        this.consumptionRepository = consumptionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long handle(AddConsumptionCommand command) {
        LocalDateTime calculatedAt = LocalDateTime.parse(
            command.calculatedAt(),
            DateTimeFormatter.ISO_DATE_TIME
        );

        ConsumptionStatus status = ConsumptionStatus.valueOf(command.status().toUpperCase());

        Consumption consumption = new Consumption(
            command.consumption(),
            command.deviceId(),
            calculatedAt,
            status
        );

        Consumption saved = consumptionRepository.save(consumption);

        if (command.consumption() > HIGH_CONSUMPTION_THRESHOLD_KW_MIN) {
            eventPublisher.publishEvent(
                new HighConsumptionDetectedEvent(command.deviceId(), command.consumption())
            );
        }

        return saved.getId();
    }
}

