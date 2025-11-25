package com.backendsems.Profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PersonName - Value Object para nombre de persona
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonName {

    private String firstName;
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}