package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.commands.AddConsumptionCommand;
import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.entities.ConsumptionStatus;
import com.backendsems.SEMS.domain.services.ConsumptionCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConsumptionCommandServiceImpl implements ConsumptionCommandService {

    private final ConsumptionRepository consumptionRepository;

    public ConsumptionCommandServiceImpl(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
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
        return saved.getId();
    }
}

