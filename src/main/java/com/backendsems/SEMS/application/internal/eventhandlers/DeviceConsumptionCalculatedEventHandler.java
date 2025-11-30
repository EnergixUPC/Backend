package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.events.DeviceConsumptionCalculatedEvent;
import org.springframework.stereotype.Component;

/**
 * DeviceConsumptionCalculatedEventHandler
 * Maneja el evento DeviceConsumptionCalculatedEvent.
 * Por ejemplo, puede enviar notificaciones o actualizar métricas externas.
 */
@Component
public class DeviceConsumptionCalculatedEventHandler {

    /**
     * Maneja el evento DeviceConsumptionCalculatedEvent.
     * @param event El evento DeviceConsumptionCalculatedEvent.
     */
    public void handle(DeviceConsumptionCalculatedEvent event) {
        // TODO: Implement logic, e.g., send alert if consumption exceeds threshold, log, or update external system.
        System.out.println("Consumption calculated for device " + event.getDeviceId() +
                ": Daily=" + event.getDailyConsumption() +
                ", Weekly=" + event.getWeeklyConsumption() +
                ", Monthly=" + event.getMonthlyConsumption());
    }
}