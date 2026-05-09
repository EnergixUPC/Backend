package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * DeviceResource
 * Recurso REST para representar un dispositivo en respuestas.
 */
public record DeviceResource(
        Long id,              // 1
        String userId,        // 2
        String nombre,        // 3
        String categoria,     // 4
        String estado,        // 5
        String ubicacion,     // 6
        boolean activo        // 7
) {
}