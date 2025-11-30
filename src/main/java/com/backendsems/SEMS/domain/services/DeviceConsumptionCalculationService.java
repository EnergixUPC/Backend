package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.entities.DeviceEntity;

/**
 * DeviceConsumptionCalculationService
 * Interfaz de servicio de dominio para calcular el consumo de energía en KWH de un dispositivo.
 */
public interface DeviceConsumptionCalculationService {

    /**
     * Calcula el consumo diario en KWH basado en la actividad del dispositivo.
     * @param device El dispositivo.
     * @return Consumo diario en KWH.
     */
    Double calculateDailyConsumption(Device device);

    /**
     * Calcula el consumo semanal en KWH.
     * @param device El dispositivo.
     * @return Consumo semanal en KWH.
     */
    Double calculateWeeklyConsumption(Device device);

    /**
     * Calcula el consumo mensual en KWH.
     * @param device El dispositivo.
     * @return Consumo mensual en KWH.
     */
    Double calculateMonthlyConsumption(Device device);

    /**
     * Crea una entidad de consumo diario.
     * @param deviceEntity La entidad del dispositivo.
     * @param device El aggregate para cálculo.
     * @return DeviceConsumptionEntity para diario.
     */
    DeviceConsumptionEntity createDailyConsumption(DeviceEntity deviceEntity, Device device);

    /**
     * Crea una entidad de consumo semanal.
     * @param deviceEntity La entidad del dispositivo.
     * @param device El aggregate para cálculo.
     * @return DeviceConsumptionEntity para semanal.
     */
    DeviceConsumptionEntity createWeeklyConsumption(DeviceEntity deviceEntity, Device device);

    /**
     * Crea una entidad de consumo mensual.
     * @param deviceEntity La entidad del dispositivo.
     * @param device El aggregate para cálculo.
     * @return DeviceConsumptionEntity para mensual.
     */
    DeviceConsumptionEntity createMonthlyConsumption(DeviceEntity deviceEntity, Device device);
}