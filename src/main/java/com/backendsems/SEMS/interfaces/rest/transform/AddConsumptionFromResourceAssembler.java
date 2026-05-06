package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.AddConsumptionCommand;
import com.backendsems.SEMS.interfaces.rest.resources.AddConsumptionResource;

public class AddConsumptionFromResourceAssembler {

    public static AddConsumptionCommand toCommand(AddConsumptionResource resource) {
        return new AddConsumptionCommand(
            resource.consumption(),
            resource.deviceId(),
            resource.calculatedAt(),
            resource.status()
        );
    }
}

