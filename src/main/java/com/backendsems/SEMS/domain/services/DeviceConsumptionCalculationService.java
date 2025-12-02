package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;

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
     * @param device El aggregate Device.
     * @return DeviceConsumption para diario.
     */
    DeviceConsumption createDailyConsumption(Device device);

    /**
     * Crea una entidad de consumo semanal.
     * @param device El aggregate Device.
     * @return DeviceConsumption para semanal.
     */
    DeviceConsumption createWeeklyConsumption(Device device);

    /**
     * Crea una entidad de consumo mensual.
     * @param device El aggregate Device.
     * @return DeviceConsumption para mensual.
     */
    DeviceConsumption createMonthlyConsumption(Device device);
}