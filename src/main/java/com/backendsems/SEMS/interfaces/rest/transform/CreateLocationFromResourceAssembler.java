package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.CreateLocationCommand;
import com.backendsems.SEMS.interfaces.rest.resources.CreateLocationResource;

/**
 * CreateLocationFromResourceAssembler
 * Ensamblador para convertir CreateLocationResource a CreateLocationCommand.
 */
public class CreateLocationFromResourceAssembler {

    /**
     * Convierte un CreateLocationResource a CreateLocationCommand.
     * @param resource El recurso de creacion.
     * @return CreateLocationCommand.
     */
    public static CreateLocationCommand toCommand(CreateLocationResource resource) {
        return new CreateLocationCommand(resource.name());
    }
}
