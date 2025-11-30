package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.interfaces.rest.resources.CreateDeviceResource;

/**
 * CreateDeviceFromResourceAssembler
 * Ensamblador para convertir CreateDeviceResource a AddDeviceCommand.
 */
public class CreateDeviceFromResourceAssembler {

    /**
     * Convierte un CreateDeviceResource a AddDeviceCommand.
     * @param resource El recurso de creación.
     * @return AddDeviceCommand.
     */
    public static AddDeviceCommand toCommand(CreateDeviceResource resource) {
        return new AddDeviceCommand(
                resource.name(),
                resource.category(),
                resource.type(),
                resource.status(),
                resource.lastActivity(),
                resource.location(),
                resource.active()
        );
    }
}