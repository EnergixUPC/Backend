package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.DeleteDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.UpdatePreferencesCommand;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.exceptions.DeviceNotFoundException;
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
    public Long handle(AddDeviceCommand command, UserId userId) {
        // Crear aggregate Device
        Device device = Device.create(command, userId);

        // Save device via repository
        device = deviceRepository.save(device);
        Long deviceId = device.getId();

        // Calcular consumo
        DeviceConsumption daily = calculationService.createDailyConsumption(device);
        DeviceConsumption weekly = calculationService.createWeeklyConsumption(device);
        DeviceConsumption monthly = calculationService.createMonthlyConsumption(device);
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
        Long userId = command.userId();
        
        // Buscar preferencias existentes o crear nuevas (comportamiento upsert)
        DevicePreference preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Crear nuevas preferencias con valores por defecto
                    return new DevicePreference(
                            userId,
                            100.0, // threshold por defecto
                            true,  // notificationEnabled por defecto
                            command.habilitarMonitoreoEnergia(),
                            command.recibirAlertasAltoConsumo(),
                            command.monitorearCalefaccionRefrigeracion(),
                            command.monitorearElectrodomesticosPrincipales(),
                            command.monitorearElectronicos(),
                            command.monitorearDispositivosCocina(),
                            command.incluirIluminacionExterior(),
                            command.rastrearEnergiaEspera(),
                            command.emailsResumenDiario(),
                            command.reportesProgresoSemanal(),
                            command.sugerirAutomizacionesAhorro(),
                            command.alertasDispositivosDesconectados()
                    );
                });

        // Si las preferencias ya existían, actualizarlas
        if (preferences.getId() != null) {
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
        }

        preferencesRepository.save(preferences);
    }

    @Override
    public void handle(DeleteDeviceCommand command) {
        Long deviceId = command.deviceId();

        if (!deviceRepository.existsById(deviceId)) {
            throw new DeviceNotFoundException(deviceId);
        }

        // Delete device and consumptions
        // Nota: Las preferencias son globales por usuario, no se eliminan al eliminar un dispositivo
        consumptionRepository.deleteByDeviceId(deviceId);
        deviceRepository.deleteById(deviceId);
    }

    // TODO: Add event publishing method
}