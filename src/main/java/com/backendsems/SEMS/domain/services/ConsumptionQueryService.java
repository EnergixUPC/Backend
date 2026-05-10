package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.queries.GetAllConsumptionsQuery;
import com.backendsems.SEMS.domain.model.queries.GetConsumptionsByDeviceIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByDeviceIdsQuery;

import java.util.List;

public interface ConsumptionQueryService {

    List<Consumption> handle(GetConsumptionsByDeviceIdQuery query);

    List<Consumption> handle(GetAllConsumptionsQuery query);

    List<Consumption> handle(GetWeeklyConsumptionByDeviceIdsQuery query);
}

