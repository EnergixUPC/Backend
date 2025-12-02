package com.backendsems.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Password {

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public Password(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.password = password;
    }

    // Note: In real implementation, password should be hashed before storage
    // This is just the value object for domain logic
}