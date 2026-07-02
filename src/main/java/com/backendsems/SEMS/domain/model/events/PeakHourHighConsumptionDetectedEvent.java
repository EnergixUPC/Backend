package com.backendsems.SEMS.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * US23: se publica cuando un registro de consumo supera el umbral configurado del usuario
 * Y ocurre dentro de su ventana de "hora punta" configurada.
 */
@Getter
public class PeakHourHighConsumptionDetectedEvent {

    private final String deviceId;
    private final Long userId;
    private final Double consumption;
    private final Double thresholdUsed;
    private final LocalDateTime calculatedAt;

    public PeakHourHighConsumptionDetectedEvent(String deviceId, Long userId, Double consumption,
                                                  Double thresholdUsed, LocalDateTime calculatedAt) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.consumption = consumption;
        this.thresholdUsed = thresholdUsed;
        this.calculatedAt = calculatedAt;
    }
}
