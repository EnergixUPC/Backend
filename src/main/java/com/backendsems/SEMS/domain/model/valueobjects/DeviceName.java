package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceName Value Object
 */
public record DeviceName(String name) {
    public DeviceName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Device name cannot be null or blank");
        }
    }
}