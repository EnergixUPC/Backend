package com.backendsems.SEMS.interfaces.rest.resources;

public record AddConsumptionResource(
    Double consumption,
    String deviceId,
    String calculatedAt,
    String status
) {
}

