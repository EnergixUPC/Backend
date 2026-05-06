package com.backendsems.SEMS.domain.model.commands;

/**
 * AddDeviceCommand
 */
public record AddDeviceCommand(
        String name,
        String category,
        String status,
        String location,
        Long userId
) {
}