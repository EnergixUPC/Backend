package com.backendsems.SEMS.domain.model.commands;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;

/**
 * Command para actualizar un Device Aggregate
 */
public record UpdateDeviceCommand(
        Long deviceId,
        String name,
        DeviceAggregate.DeviceType type,
        Long userId
) {
    /**
     * Constructor que incluye validaciones
     */
    public UpdateDeviceCommand {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("ID de dispositivo válido es requerido");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del dispositivo es requerido");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Tipo de dispositivo es requerido");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}