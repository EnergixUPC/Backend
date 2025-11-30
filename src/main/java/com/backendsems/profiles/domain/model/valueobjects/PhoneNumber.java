package com.backendsems.Profile.domain.model.valueobjects;

/**
 * PhoneNumber Value Object
 */
public record PhoneNumber(String number) {
    public PhoneNumber {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        // Simple validation: assume it's a string of digits, possibly with + and spaces
        if (!number.matches("\\+?\\d[\\d\\s]*")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
}