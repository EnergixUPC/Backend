package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceType Value Object
 */
public record DeviceType(String type) {
    public DeviceType {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Device type cannot be null or blank");
        }
    }
}