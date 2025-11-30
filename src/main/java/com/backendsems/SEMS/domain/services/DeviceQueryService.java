package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.entities.PreferencesEntity;
import com.backendsems.SEMS.domain.model.queries.*;

import java.util.List;

/**
 * DeviceQueryService
 * Interfaz de servicio de dominio para queries relacionadas con dispositivos.
 */
public interface DeviceQueryService {

    /**
     * Maneja la query para obtener todos los dispositivos.
     * @param query La query GetAllDevicesQuery.
     * @return Lista de dispositivos.
     */
    List<Device> handle(GetAllDevicesQuery query);

    /**
     * Maneja la query para obtener un dispositivo por ID.
     * @param query La query GetDeviceByIdQuery.
     * @return El dispositivo, o null si no existe.
     */
    Device handle(GetDeviceByIdQuery query);

    /**
     * Maneja la query para obtener dispositivos por userId.
     * @param query La query GetDevicesByUserIdQuery.
     * @return Lista de dispositivos del usuario.
     */
    List<Device> handle(GetDevicesByUserIdQuery query);

    /**
     * Maneja la query para verificar si un dispositivo existe.
     * @param query La query DeviceExistsQuery.
     * @return true si existe, false otherwise.
     */
    boolean handle(DeviceExistsQuery query);

    /**
     * Maneja la query para obtener consumo por deviceId.
     * @param query La query GetDeviceConsumptionByDeviceIdQuery.
     * @return Lista de consumos.
     */
    List<DeviceConsumptionEntity> handle(GetDeviceConsumptionByDeviceIdQuery query);

    /**
     * Maneja la query para obtener preferencias por userId y deviceId.
     * @param query La query GetPreferencesByUserIdAndDeviceIdQuery.
     * @return Las preferencias, o null si no existen.
     */
    PreferencesEntity handle(GetPreferencesByUserIdAndDeviceIdQuery query);

    /**
     * Maneja la query para verificar si existen preferencias.
     * @param query La query PreferencesExistQuery.
     * @return true si existen, false otherwise.
     */
    boolean handle(PreferencesExistQuery query);

    /**
     * Maneja la query para obtener todas las preferencias de un usuario.
     * @param query La query GetAllPreferencesByUserIdQuery.
     * @return Lista de preferencias.
     */
    List<PreferencesEntity> handle(GetAllPreferencesByUserIdQuery query);
}