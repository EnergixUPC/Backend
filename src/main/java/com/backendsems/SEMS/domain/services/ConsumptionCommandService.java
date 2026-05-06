package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.commands.AddConsumptionCommand;

public interface ConsumptionCommandService {

    Long handle(AddConsumptionCommand command);
}

