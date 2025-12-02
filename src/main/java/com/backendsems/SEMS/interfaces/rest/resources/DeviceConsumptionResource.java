package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * DeviceConsumptionResource
 * Recurso REST para representar el consumo de un dispositivo.
 */
public record DeviceConsumptionResource(
        Long id,
        Long deviceId,
        String period,
        Double consumption
) {
}