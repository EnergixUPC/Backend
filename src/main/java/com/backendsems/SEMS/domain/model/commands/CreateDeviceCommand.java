package com.backendsems.SEMS.domain.model.commands;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;

/**
 * Command para crear un nuevo Device Aggregate
 */
public record CreateDeviceCommand(
        String name,
        DeviceAggregate.DeviceType type,
        Long userId
) {
    /**
     * Constructor que incluye validaciones
     */
    public CreateDeviceCommand {
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