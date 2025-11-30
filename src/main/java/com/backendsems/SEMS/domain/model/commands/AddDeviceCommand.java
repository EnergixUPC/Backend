package com.backendsems.SEMS.domain.model.commands;

/**
 * AddDeviceCommand
 */
public record AddDeviceCommand(
        String name,
        String category,
        String type,
        String status,
        String lastActivity,
        String location,
        boolean active
) {
}