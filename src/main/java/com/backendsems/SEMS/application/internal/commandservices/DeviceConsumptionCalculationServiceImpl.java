package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.domain.services.DeviceConsumptionCalculationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * DeviceConsumptionCalculationServiceImpl
 * Implementación del servicio de dominio para calcular consumo.
 */
@Service
public class DeviceConsumptionCalculationServiceImpl implements DeviceConsumptionCalculationService {

    @Override
    public Double calculateDailyConsumption(Device device) {
        // Lógica simplificada: basado en activity (ej. si está activo, consumo base)
        double baseConsumption = 1.0; // KWH por día base
        if (device.getActivity().activity().equals("high")) {
            baseConsumption *= 2.0;
        } else if (device.getActivity().activity().equals("low")) {
            baseConsumption *= 0.5;
        }
        return baseConsumption;
    }

    @Override
    public Double calculateWeeklyConsumption(Device device) {
        return calculateDailyConsumption(device) * 7;
    }

    @Override
    public Double calculateMonthlyConsumption(Device device) {
        return calculateDailyConsumption(device) * 30; // Aproximado
    }

    @Override
    public DeviceConsumption createDailyConsumption(Device device) {
        Double consumo = calculateDailyConsumption(device);
        return new DeviceConsumption(device, consumo, "daily", LocalDate.now());
    }

    @Override
    public DeviceConsumption createWeeklyConsumption(Device device) {
        Double consumo = calculateWeeklyConsumption(device);
        return new DeviceConsumption(device, consumo, "weekly", LocalDate.now());
    }

    @Override
    public DeviceConsumption createMonthlyConsumption(Device device) {
        Double consumo = calculateMonthlyConsumption(device);
        return new DeviceConsumption(device, consumo, "monthly", LocalDate.now());
    }
}