package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * DeviceId Value Object
 */
@Embeddable
public record DeviceId(Long value) {
    public DeviceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Device ID must be a positive number");
        }
    }
}