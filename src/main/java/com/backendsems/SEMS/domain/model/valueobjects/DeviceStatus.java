package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeviceStatus Value Object
 * Representa el estado de un dispositivo
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatus {
    
    private boolean active;
    
    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    @Override
    public String toString() {
        return active ? "ACTIVE" : "INACTIVE";
    }
}