package com.backendsems.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Phone {

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    public Phone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phone = phone;
    }

    @Override
    public String toString() {
        return phone;
    }
}