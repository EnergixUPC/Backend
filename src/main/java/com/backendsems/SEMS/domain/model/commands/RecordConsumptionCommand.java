package com.backendsems.SEMS.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RecordConsumptionCommand
 * Comando para registrar una lectura de consumo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordConsumptionCommand {
    
    private Long deviceId;
    private Double consumptionValue;
    private LocalDateTime readingTime;
    
    public void validate() {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("ID de dispositivo válido es requerido");
        }
        
        if (consumptionValue == null || consumptionValue < 0) {
            throw new IllegalArgumentException("Valor de consumo debe ser mayor o igual a 0");
        }
        
        if (readingTime == null) {
            readingTime = LocalDateTime.now();
        }
        
        if (readingTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La hora de lectura no puede ser futura");
        }
    }
}