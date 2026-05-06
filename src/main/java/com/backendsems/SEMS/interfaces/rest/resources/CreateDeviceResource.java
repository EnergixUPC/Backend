package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * CreateDeviceResource
 * Recurso REST para crear un dispositivo.
 */
public record CreateDeviceResource(
        String name,
        String category,
        String status,
        String location,
        Long userId
) {
}