package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.DeleteDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.UpdatePreferencesCommand;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * DeviceCommandService
 * Interfaz de servicio de dominio para comandos relacionados con dispositivos.
 */
public interface DeviceCommandService {

    /**
     * Maneja el comando para agregar un dispositivo.
     * @param command El comando AddDeviceCommand.
     * @param userId El ID del usuario.
     * @return El ID del dispositivo creado.
     */
    Long handle(AddDeviceCommand command, UserId userId);

    /**
     * Maneja el comando para actualizar preferencias de un dispositivo.
     * @param command El comando UpdatePreferencesCommand.
     */
    void handle(UpdatePreferencesCommand command);

    /**
     * Maneja el comando para eliminar un dispositivo.
     * @param command El comando DeleteDeviceCommand.
     */
    void handle(DeleteDeviceCommand command);
}