package com.backendsems.SEMS.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HighConsumptionDetectedEvent
 * Evento de dominio que se dispara cuando se detecta un consumo alto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HighConsumptionDetectedEvent {
    
    private Long deviceId;
    private String deviceName;
    private Double consumptionValue;
    private Double threshold;
    private Long userId;
    private LocalDateTime occurredOn;
    
    public static HighConsumptionDetectedEvent create(
            Long deviceId, 
            String deviceName, 
            Double consumptionValue, 
            Double threshold,
            Long userId) {
        return HighConsumptionDetectedEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .consumptionValue(consumptionValue)
                .threshold(threshold)
                .userId(userId)
                .occurredOn(LocalDateTime.now())
                .build();
    }
}