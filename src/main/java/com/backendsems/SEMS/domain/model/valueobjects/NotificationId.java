package com.backendsems.SEMS.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NotificationId Value Object
 * Representa un identificador único de notificación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationId {
    
    private Long value;
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}