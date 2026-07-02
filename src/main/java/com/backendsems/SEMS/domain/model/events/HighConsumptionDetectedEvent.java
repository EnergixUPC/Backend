package com.backendsems.SEMS.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Se publica cuando un registro de consumo supera el umbral configurado (por defecto 5 kW/min,
 * o el umbral propio del usuario si lo configuró — ver US23).
 */
@Getter
public class HighConsumptionDetectedEvent {

    private final String deviceId;
    private final Double consumption;
    private final Double thresholdUsed;
    private final LocalDateTime calculatedAt;

    public HighConsumptionDetectedEvent(String deviceId, Double consumption, Double thresholdUsed, LocalDateTime calculatedAt) {
        this.deviceId = deviceId;
        this.consumption = consumption;
        this.thresholdUsed = thresholdUsed;
        this.calculatedAt = calculatedAt;
    }
}