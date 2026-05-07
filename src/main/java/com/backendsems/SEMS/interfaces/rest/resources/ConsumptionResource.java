package com.backendsems.SEMS.interfaces.rest.resources;

public record ConsumptionResource(
    Long id,
    Double consumption,
    String deviceId,
    String calculatedAt,
    String status
) {
}

