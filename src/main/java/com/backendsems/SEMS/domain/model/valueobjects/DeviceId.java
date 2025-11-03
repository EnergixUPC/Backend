package com.backendsems.SEMS.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeviceId Value Object
 * Representa un identificador único de dispositivo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceId {
    
    private Long value;
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}