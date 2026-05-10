package com.backendsems.SEMS.domain.model.commands;

public record UpdateDeviceCommand(
        Long deviceId,
        String status,
        String name,
        String category,
        String location,
        Boolean active
) {
}
