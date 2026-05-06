package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.interfaces.rest.resources.CreateDeviceResource;

public class CreateDeviceFromResourceAssembler {
    public static AddDeviceCommand toCommandFromResource(CreateDeviceResource resource) {
        return new AddDeviceCommand(
                resource.name(),
                resource.category(),
                resource.status(),
                resource.location(),
                resource.userId()
        );
    }
}