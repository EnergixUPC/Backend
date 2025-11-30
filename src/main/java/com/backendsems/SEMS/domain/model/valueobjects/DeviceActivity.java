package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceActivity Value Object
 */
public record DeviceActivity(String activity) {
    public DeviceActivity {
        if (activity == null || activity.isBlank()) {
            throw new IllegalArgumentException("Device activity cannot be null or blank");
        }
    }
}