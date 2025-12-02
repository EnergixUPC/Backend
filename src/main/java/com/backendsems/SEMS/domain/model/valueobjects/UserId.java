package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * UserId Value Object
 */
@Embeddable
public record UserId(Long id) {
    public UserId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}