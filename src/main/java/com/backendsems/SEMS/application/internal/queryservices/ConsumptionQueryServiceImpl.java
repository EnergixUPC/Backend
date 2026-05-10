package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.queries.GetAllConsumptionsQuery;
import com.backendsems.SEMS.domain.model.queries.GetConsumptionsByDeviceIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByDeviceIdsQuery;
import com.backendsems.SEMS.domain.services.ConsumptionQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumptionQueryServiceImpl implements ConsumptionQueryService {

    private final ConsumptionRepository consumptionRepository;

    public ConsumptionQueryServiceImpl(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }

    @Override
    public List<Consumption> handle(GetConsumptionsByDeviceIdQuery query) {
        return consumptionRepository.findByDeviceId(query.deviceId());
    }

    @Override
    public List<Consumption> handle(GetAllConsumptionsQuery query) {
        return consumptionRepository.findAll();
    }

    @Override
    public List<Consumption> handle(GetWeeklyConsumptionByDeviceIdsQuery query) {
        if (query.deviceIds().isEmpty()) return List.of();
        return consumptionRepository.findByDeviceIdInAndCalculatedAtBetween(
                query.deviceIds(), query.weekStart(), query.weekEnd());
    }
}

