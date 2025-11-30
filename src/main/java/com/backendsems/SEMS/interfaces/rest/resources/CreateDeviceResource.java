package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * CreateDeviceResource
 * Recurso REST para crear un dispositivo.
 */
public record CreateDeviceResource(
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