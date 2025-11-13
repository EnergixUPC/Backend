package com.backendsems.SEMS.domain.model.commands;

/**
 * Command para eliminar un Device Aggregate
 */
public record DeleteDeviceCommand(
        Long deviceId,
        Long userId
) {
    /**
     * Constructor que incluye validaciones
     */
    public DeleteDeviceCommand {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("ID de dispositivo válido es requerido");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}