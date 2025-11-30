package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.DeleteDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.UpdatePreferencesCommand;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.entities.DeviceEntity;
import com.backendsems.SEMS.domain.model.entities.PreferencesEntity;
import com.backendsems.SEMS.domain.model.events.DeviceAddedEvent;
import com.backendsems.SEMS.domain.model.events.DeviceConsumptionCalculatedEvent;
import com.backendsems.SEMS.domain.exceptions.DeviceNotFoundException;
import com.backendsems.SEMS.domain.exceptions.PreferencesNotFoundException;
import com.backendsems.SEMS.domain.services.DeviceCommandService;
import com.backendsems.SEMS.domain.services.DeviceConsumptionCalculationService;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceConsumptionRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.PreferencesRepository;
import org.springframework.stereotype.Service;

/**
 * DeviceCommandServiceImpl
 * Implementación del servicio de comandos para dispositivos.
 */
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private final DeviceConsumptionCalculationService calculationService;
    private final DeviceRepository deviceRepository;
    private final PreferencesRepository preferencesRepository;
    private final DeviceConsumptionRepository consumptionRepository;

    public DeviceCommandServiceImpl(DeviceConsumptionCalculationService calculationService, DeviceRepository deviceRepository, PreferencesRepository preferencesRepository, DeviceConsumptionRepository consumptionRepository) {
        this.calculationService = calculationService;
        this.deviceRepository = deviceRepository;
        this.preferencesRepository = preferencesRepository;
        this.consumptionRepository = consumptionRepository;
    }

    @Override
    public Long handle(AddDeviceCommand command) {
        // Crear aggregate Device
        UserId userId = new UserId(Long.parseLong(command.userId()));
        Device device = Device.create(command, userId);

        // Save device via repository
        DeviceEntity deviceEntity = DeviceEntity.fromAggregate(device);
        deviceRepository.save(deviceEntity);
        Long deviceId = deviceEntity.getId();

        // Calcular consumo
        DeviceConsumptionEntity daily = calculationService.createDailyConsumption(deviceEntity, device);
        DeviceConsumptionEntity weekly = calculationService.createWeeklyConsumption(deviceEntity, device);
        DeviceConsumptionEntity monthly = calculationService.createMonthlyConsumption(deviceEntity, device);
        consumptionRepository.save(daily);
        consumptionRepository.save(weekly);
        consumptionRepository.save(monthly);

        // Publicar eventos (TODO: Implement event publishing)
        // publishEvent(new DeviceAddedEvent(userId, deviceId, device.getName(), device.getCategory(), device.getType(), device.getStatus(), device.getActivity(), device.getLocation(), device.isActivo()));
        // publishEvent(new DeviceConsumptionCalculatedEvent(userId, deviceId, calculationService.calculateDailyConsumption(device), calculationService.calculateWeeklyConsumption(device), calculationService.calculateMonthlyConsumption(device)));

        return deviceId;
    }

    @Override
    public void handle(UpdatePreferencesCommand command) {
        Long deviceId = command.deviceId();
        DeviceEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
        UserId userId = device.getUserId();
        PreferencesEntity preferences = preferencesRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new PreferencesNotFoundException(userId, deviceId));

        preferences.updateHabilitarMonitoreoEnergia(command.habilitarMonitoreoEnergia());
        preferences.updateRecibirAlertasAltoConsumo(command.recibirAlertasAltoConsumo());
        preferences.updateMonitorearCalefaccionRefrigeracion(command.monitorearCalefaccionRefrigeracion());
        preferences.updateMonitorearElectrodomesticosPrincipales(command.monitorearElectrodomesticosPrincipales());
        preferences.updateMonitorearElectronicos(command.monitorearElectronicos());
        preferences.updateMonitorearDispositivosCocina(command.monitorearDispositivosCocina());
        preferences.updateIncluirIluminacionExterior(command.incluirIluminacionExterior());
        preferences.updateRastrearEnergiaEspera(command.rastrearEnergiaEspera());
        preferences.updateEmailsResumenDiario(command.emailsResumenDiario());
        preferences.updateReportesProgresoSemanal(command.reportesProgresoSemanal());
        preferences.updateSugerirAutomizacionesAhorro(command.sugerirAutomizacionesAhorro());
        preferences.updateAlertasDispositivosDesconectados(command.alertasDispositivosDesconectados());

        preferencesRepository.save(preferences);
    }

    @Override
    public void handle(DeleteDeviceCommand command) {
        Long deviceId = command.deviceId();

        if (!deviceRepository.existsById(deviceId)) {
            throw new DeviceNotFoundException(deviceId);
        }

        // Delete device, consumptions, preferences
        consumptionRepository.deleteByDeviceId(deviceId); // Assuming custom method
        preferencesRepository.deleteByDeviceId(deviceId); // Assuming custom method
        deviceRepository.deleteById(deviceId);
    }

    // TODO: Add event publishing method
}