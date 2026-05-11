package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.commands.CreateLocationCommand;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * LocationCommandService
 * Servicio de comandos para ubicaciones.
 */
public interface LocationCommandService {

    /**
     * Maneja el comando para crear una ubicacion.
     * @param command El comando CreateLocationCommand.
     * @param userId El ID del usuario.
     * @return El ID de la ubicacion creada.
     */
    Long handle(CreateLocationCommand command, UserId userId);
}
