package com.backendsems.profiles.domain.model.valueobjects;

/**
 * Address Value Object
 */
public record Address(String address) {
    public Address {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be null or blank");
        }
    }
}