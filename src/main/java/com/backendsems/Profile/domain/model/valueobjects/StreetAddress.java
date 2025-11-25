package com.backendsems.Profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StreetAddress - Value Object para dirección
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreetAddress {

    private String address;
}