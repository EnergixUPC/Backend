package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.SEMS.domain.model.commands.AddConsumptionCommand;
import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.entities.ConsumptionStatus;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.events.PeakHourHighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.events.ConsumptionRecordedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.ConsumptionCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.SettingsRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConsumptionCommandServiceImpl implements ConsumptionCommandService {

    // Umbral global por defecto (kW/min) cuando el usuario no configuró uno propio (ver US23).
    static final double HIGH_CONSUMPTION_THRESHOLD_KW_MIN = 5.0;

    private final ConsumptionRepository consumptionRepository;
    private final DeviceRepository deviceRepository;
    private final SettingsRepository settingsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConsumptionCommandServiceImpl(ConsumptionRepository consumptionRepository,
                                         DeviceRepository deviceRepository,
                                         SettingsRepository settingsRepository,
                                         ApplicationEventPublisher eventPublisher) {
        this.consumptionRepository = consumptionRepository;
        this.deviceRepository = deviceRepository;
        this.settingsRepository = settingsRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long handle(AddConsumptionCommand command) {
        LocalDateTime calculatedAt = LocalDateTime.parse(
            command.calculatedAt(),
            DateTimeFormatter.ISO_DATE_TIME
        );

        ConsumptionStatus status = ConsumptionStatus.valueOf(command.status().toUpperCase());

        Consumption consumption = new Consumption(
            command.consumption(),
            command.deviceId(),
            calculatedAt,
            status
        );

        Consumption saved = consumptionRepository.save(consumption);

        // US23: resolver al usuario dueño del dispositivo para aplicar su umbral y horario punta propios.
        UserId ownerId = resolveOwnerUserId(command.deviceId());
        UserSetting settings = ownerId != null
                ? settingsRepository.findByUserId(ownerId).orElse(null)
                : null;

        double effectiveThreshold = (settings != null && settings.getHighConsumptionThresholdKwh() != null)
                ? settings.getHighConsumptionThresholdKwh()
                : HIGH_CONSUMPTION_THRESHOLD_KW_MIN;

        if (command.consumption() > effectiveThreshold) {
            eventPublisher.publishEvent(
                new HighConsumptionDetectedEvent(command.deviceId(), command.consumption(), effectiveThreshold, calculatedAt)
            );

            boolean isPeakHour = settings != null && settings.isWithinPeakHour(calculatedAt.toLocalTime());
            if (isPeakHour) {
                eventPublisher.publishEvent(new PeakHourHighConsumptionDetectedEvent(
                        command.deviceId(), ownerId.id(), command.consumption(), effectiveThreshold, calculatedAt
                ));
            }
        }

        eventPublisher.publishEvent(new ConsumptionRecordedEvent(command.deviceId(), command.consumption(), command.calculatedAt()));

        return saved.getId();
    }

    private UserId resolveOwnerUserId(String deviceId) {
        try {
            Long id = Long.parseLong(deviceId);
            return deviceRepository.findById(id).map(device -> device.getUserId()).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


