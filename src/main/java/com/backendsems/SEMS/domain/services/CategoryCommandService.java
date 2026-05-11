package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.commands.CreateCategoryCommand;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * CategoryCommandService
 * Servicio de comandos para categorias.
 */
public interface CategoryCommandService {

    /**
     * Maneja el comando para crear una categoria.
     * @param command El comando CreateCategoryCommand.
     * @param userId El ID del usuario.
     * @return El ID de la categoria creada.
     */
    Long handle(CreateCategoryCommand command, UserId userId);
}
