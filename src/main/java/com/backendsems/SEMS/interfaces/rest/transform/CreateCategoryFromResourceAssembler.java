package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.CreateCategoryCommand;
import com.backendsems.SEMS.interfaces.rest.resources.CreateCategoryResource;

/**
 * CreateCategoryFromResourceAssembler
 * Ensamblador para convertir CreateCategoryResource a CreateCategoryCommand.
 */
public class CreateCategoryFromResourceAssembler {

    /**
     * Convierte un CreateCategoryResource a CreateCategoryCommand.
     * @param resource El recurso de creacion.
     * @return CreateCategoryCommand.
     */
    public static CreateCategoryCommand toCommand(CreateCategoryResource resource) {
        return new CreateCategoryCommand(resource.name());
    }
}
