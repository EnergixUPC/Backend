package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * EnergyConsumption Value Object
 * Representa el consumo de energía en kWh
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumption {
    
    private Double value; // kWh
    
    public Double getValue() {
        return value;
    }
    
    public EnergyConsumption add(EnergyConsumption other) {
        return new EnergyConsumption(this.value + other.value);
    }
    
    public EnergyConsumption subtract(EnergyConsumption other) {
        double result = this.value - other.value;
        return new EnergyConsumption(Math.max(0, result));
    }
    
    public BigDecimal getValueWithPrecision() {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
    
    public boolean isHighConsumption() {
        return value > 100.0; // Más de 100 kWh
    }
    
    @Override
    public String toString() {
        return String.format("%.2f kWh", value);
    }
}