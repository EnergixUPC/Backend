package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceCategory Value Object
 */
public record  DeviceCategory(String category) {
    public DeviceCategory {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Device category cannot be null or blank");
        }
    }
}