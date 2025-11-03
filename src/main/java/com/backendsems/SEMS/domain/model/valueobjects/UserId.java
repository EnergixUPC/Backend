package com.backendsems.SEMS.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserId Value Object
 * Representa un identificador único de usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserId {
    
    private Long value;
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}