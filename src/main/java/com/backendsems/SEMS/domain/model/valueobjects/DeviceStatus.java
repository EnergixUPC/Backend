package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * DeviceStatus Value Object
 */
public record DeviceStatus(String status) {
    public DeviceStatus {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Device status cannot be null or blank");
        }
    }
}