package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email Value Object
 * Representa una dirección de email válida
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    
    private String value;
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}