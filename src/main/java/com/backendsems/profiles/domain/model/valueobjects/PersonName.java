package com.backendsems.profiles.domain.model.valueobjects;

/**
 * PersonName Value Object
 */
public record PersonName(String name) {
    public PersonName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
    }
}