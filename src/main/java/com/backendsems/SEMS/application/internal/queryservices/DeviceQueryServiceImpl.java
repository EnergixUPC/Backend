package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.queries.*;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceConsumptionRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.PreferencesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * DeviceQueryServiceImpl
 * Implementación del servicio de queries para dispositivos.
 */
@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final DeviceRepository deviceRepository;
    private final PreferencesRepository preferencesRepository;
    private final DeviceConsumptionRepository consumptionRepository;

    public DeviceQueryServiceImpl(DeviceRepository deviceRepository, PreferencesRepository preferencesRepository, DeviceConsumptionRepository consumptionRepository) {
        this.deviceRepository = deviceRepository;
        this.preferencesRepository = preferencesRepository;
        this.consumptionRepository = consumptionRepository;
    }

    @Override
    public List<Device> handle(GetAllDevicesQuery query) {
        return deviceRepository.findAll();
    }

    @Override
    public Device handle(GetDeviceByIdQuery query) {
        return deviceRepository.findById(query.deviceId())
                .orElse(null);
    }

    @Override
    public List<Device> handle(GetDevicesByUserIdQuery query) {
        return deviceRepository.findByUserId(query.userId());
    }

    @Override
    public boolean handle(DeviceExistsQuery query) {
        return deviceRepository.existsById(query.deviceId());
    }

    @Override
    public List<DeviceConsumption> handle(GetDeviceConsumptionByDeviceIdQuery query) {
        return consumptionRepository.findByDeviceId(query.deviceId());
    }

    @Override
    public DevicePreference handle(GetPreferencesByUserIdAndDeviceIdQuery query) {
        // Ahora las preferencias son globales por usuario, ignoramos deviceId
        return preferencesRepository.findByUserId(query.userId()).orElse(null);
    }

    @Override
    public boolean handle(PreferencesExistQuery query) {
        // Ahora las preferencias son globales por usuario, ignoramos deviceId
        return preferencesRepository.existsByUserId(query.userId());
    }

    @Override
    public List<DevicePreference> handle(GetAllPreferencesByUserIdQuery query) {
        // findByUserId retorna Optional, convertimos a lista
        return preferencesRepository.findByUserId(query.userId())
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public List<DeviceConsumption> handle(GetTopDevicesByUserQuery query) {
        Pageable pageable = PageRequest.of(0, query.limit());
        return consumptionRepository.findTopConsumptionByUserIdAndPeriod(query.userId(), query.period(), pageable);
    }

    @Override
    public List<DeviceConsumption> handle(GetWeeklyConsumptionByUserQuery query) {
        // Obtener la semana actual (de lunes a domingo)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        // Usar el nuevo método que agrupa por fecha
        return consumptionRepository.findDailyConsumptionByUserIdAndDateRange(
            query.userId(), weekStart, weekEnd);
    }

    @Override
    public List<Object[]> handleDailySummary(GetWeeklyConsumptionByUserQuery query) {
        // Obtener la semana actual (de lunes a domingo)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        // Usar el método que agrupa y suma por día
        return consumptionRepository.findDailyConsumptionSumByUserIdAndDateRange(
            query.userId(), weekStart, weekEnd);
    }
}