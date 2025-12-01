package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.entities.PreferencesEntity;
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
import java.util.stream.Collectors;

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
        return deviceRepository.findAll().stream()
                .map(Device::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Device handle(GetDeviceByIdQuery query) {
        return deviceRepository.findById(query.deviceId())
                .map(Device::fromEntity)
                .orElse(null);
    }

    @Override
    public List<Device> handle(GetDevicesByUserIdQuery query) {
        return deviceRepository.findByUserId(query.userId()).stream()
                .map(Device::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean handle(DeviceExistsQuery query) {
        return deviceRepository.existsById(query.deviceId());
    }

    @Override
    public List<DeviceConsumptionEntity> handle(GetDeviceConsumptionByDeviceIdQuery query) {
        return consumptionRepository.findByDeviceId(query.deviceId());
    }

    @Override
    public PreferencesEntity handle(GetPreferencesByUserIdAndDeviceIdQuery query) {
        return preferencesRepository.findByUserIdAndDeviceId(query.userId(), query.deviceId()).orElse(null);
    }

    @Override
    public boolean handle(PreferencesExistQuery query) {
        return preferencesRepository.existsByUserIdAndDeviceId(query.userId(), query.deviceId());
    }

    @Override
    public List<PreferencesEntity> handle(GetAllPreferencesByUserIdQuery query) {
        return preferencesRepository.findByUserId(query.userId());
    }

    @Override
    public List<DeviceConsumptionEntity> handle(GetTopDevicesByUserQuery query) {
        Pageable pageable = PageRequest.of(0, query.limit());
        return consumptionRepository.findTopConsumptionByUserIdAndPeriod(query.userId(), query.period(), pageable);
    }

    @Override
    public List<DeviceConsumptionEntity> handle(GetWeeklyConsumptionByUserQuery query) {
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