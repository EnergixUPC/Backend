package com.backendsems.SEMS.domain.model.commands;

public record AddConsumptionCommand(
        Double consumption,
        String deviceId,
        String calculatedAt,
        String status
) {
}