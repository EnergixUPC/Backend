package com.backendsems.profiles.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * EmailAddress Value Object
 */
public record EmailAddress(String address) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public EmailAddress {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}