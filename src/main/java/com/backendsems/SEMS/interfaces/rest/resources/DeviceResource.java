package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * DeviceResource
 * Recurso REST para representar un dispositivo en respuestas.
 */
public record DeviceResource(
        Long id,
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