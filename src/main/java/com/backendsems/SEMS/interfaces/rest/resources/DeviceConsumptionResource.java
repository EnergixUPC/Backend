package com.backendsems.SEMS.interfaces.rest.resources;

import java.util.Date;

/**
 * DeviceConsumptionResource
 * Recurso REST para representar el consumo de un dispositivo.
 */
public record DeviceConsumptionResource(
        Long id,
        Long deviceId,
        String period,
        Double consumption,
        Date createdAt
) {
}