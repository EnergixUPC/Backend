package com.backendsems.SEMS.domain.model.events;

import lombok.Getter;

/**
 * Se publica cuando un registro de consumo supera el umbral de 5 kW/min.
 */
@Getter
public class HighConsumptionDetectedEvent {

    private final String deviceId;
    private final Double consumption;

    public HighConsumptionDetectedEvent(String deviceId, Double consumption) {
        this.deviceId = deviceId;
        this.consumption = consumption;
    }
}