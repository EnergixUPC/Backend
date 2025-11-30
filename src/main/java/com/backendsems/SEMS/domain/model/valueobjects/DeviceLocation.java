package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceLocation Value Object
 */
public record DeviceLocation(String location) {
    public DeviceLocation {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Device location cannot be null or blank");
        }
    }
}