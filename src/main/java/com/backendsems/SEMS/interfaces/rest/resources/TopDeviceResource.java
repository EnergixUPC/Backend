package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * TopDeviceResource
 * Recurso REST para representar un dispositivo en el ranking de consumo.
 */
public record TopDeviceResource(
    Long deviceId,
    String deviceName,
    String deviceType,
    String deviceCategory,
    Double totalConsumption,
    String period
) {
}