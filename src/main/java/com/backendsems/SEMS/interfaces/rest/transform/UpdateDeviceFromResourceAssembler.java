package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.UpdateDeviceCommand;
import com.backendsems.SEMS.interfaces.rest.resources.UpdateDeviceResource;

public class UpdateDeviceFromResourceAssembler {

    public static UpdateDeviceCommand toCommand(Long deviceId, UpdateDeviceResource resource) {
        return new UpdateDeviceCommand(
                deviceId,
                resource.status(),
                resource.name(),
                resource.category(),
                resource.location(),
                resource.active()
        );
    }
}
