package com.backendsems.Profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EmailAddress - Value Object para dirección de email
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddress {

    private String email;

    // Validación básica
    public boolean isValid() {
        return email != null && email.contains("@");
    }
}