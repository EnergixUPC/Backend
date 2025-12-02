package com.backendsems.SEMS.domain.model.events;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import lombok.Getter;

/**
 * DeviceConsumptionCalculatedEvent
 * Evento de dominio que se dispara cuando se calcula el consumo de un dispositivo.
 */
@Getter
public class DeviceConsumptionCalculatedEvent {
    private final UserId userId;
    private final Long deviceId;
    private final Double dailyConsumption;
    private final Double weeklyConsumption;
    private final Double monthlyConsumption;

    public DeviceConsumptionCalculatedEvent(UserId userId, Long deviceId, Double dailyConsumption, Double weeklyConsumption, Double monthlyConsumption) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.dailyConsumption = dailyConsumption;
        this.weeklyConsumption = weeklyConsumption;
        this.monthlyConsumption = monthlyConsumption;
    }
}