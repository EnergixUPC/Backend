package com.backendsems.SEMS.domain.model.valueobjects;

/**
 * UserId Value Object
 */
public record UserId(Long id) {
    public UserId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}