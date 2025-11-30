package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.DeviceConsumptionCalculatedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * DeviceConsumptionCalculatedEventHandler
 * Maneja el evento DeviceConsumptionCalculatedEvent.
 * Envía notificaciones/alertas basadas en niveles de consumo.
 */
@Component
public class DeviceConsumptionCalculatedEventHandler {

    private final NotificationCommandService notificationCommandService;

    public DeviceConsumptionCalculatedEventHandler(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    /**
     * Maneja el evento DeviceConsumptionCalculatedEvent.
     * @param event El evento DeviceConsumptionCalculatedEvent.
     */
    @EventListener
    public void handle(DeviceConsumptionCalculatedEvent event) {
        System.out.println("Consumption calculated for device " + event.getDeviceId() +
                ": Daily=" + event.getDailyConsumption() +
                ", Weekly=" + event.getWeeklyConsumption() +
                ", Monthly=" + event.getMonthlyConsumption());

        // Lógica de alertas basada en consumo diario
        double daily = event.getDailyConsumption();
        String type;
        String message;

        if (daily > 15.0) {
            type = "ALERT";
            message = "Alerta crítica: Consumo diario excesivo de " + daily + " kWh para el dispositivo " + event.getDeviceId();
        } else if (daily > 10.0) {
            type = "WARNING";
            message = "Advertencia: Consumo diario alto de " + daily + " kWh para el dispositivo " + event.getDeviceId();
        } else if (daily > 5.0) {
            type = "INFO";
            message = "Información: Consumo diario moderado de " + daily + " kWh para el dispositivo " + event.getDeviceId();
        } else {
            return; // No enviar notificación si consumo bajo
        }

        CreateNotificationCommand command = new CreateNotificationCommand(
                new DeviceId(event.getDeviceId()),
                event.getUserId(),
                message,
                type
        );
        notificationCommandService.createNotification(command);
    }
}