package com.backendsems.SEMS.domain.model.commands;

/**
 * AddDeviceCommand
 */
public record AddDeviceCommand(
        String userId,
        String nombre,
        String categoria,
        String tipo,
        String estado,
        String ultimaActividad,
        String ubicacion,
        boolean activo
) {
}