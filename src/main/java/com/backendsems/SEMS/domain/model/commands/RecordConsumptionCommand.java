package com.backendsems.SEMS.domain.model.commands;

import java.time.LocalDateTime;

/**
 * Command para registrar una lectura de consumo
 */
public record RecordConsumptionCommand(
        Long deviceId,
        Double consumptionValue,
        LocalDateTime readingTime
) {
    /**
     * Constructor que incluye validaciones
     */
    public RecordConsumptionCommand {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("ID de dispositivo válido es requerido");
        }
        
        if (consumptionValue == null || consumptionValue < 0) {
            throw new IllegalArgumentException("Valor de consumo debe ser mayor o igual a 0");
        }
        
        // Si readingTime es null, usar el tiempo actual
        if (readingTime == null) {
            readingTime = LocalDateTime.now();
        }
        
        if (readingTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La hora de lectura no puede ser futura");
        }
    }
}