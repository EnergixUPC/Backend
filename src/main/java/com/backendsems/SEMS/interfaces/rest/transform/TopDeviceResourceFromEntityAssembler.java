package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.interfaces.rest.resources.TopDeviceResource;

/**
 * TopDeviceResourceFromEntityAssembler
 * Ensamblador para convertir DeviceConsumptionEntity a TopDeviceResource.
 */
public class TopDeviceResourceFromEntityAssembler {

    /**
     * Convierte una entidad de consumo a un recurso de dispositivo top.
     * @param entity La entidad DeviceConsumptionEntity.
     * @return El recurso TopDeviceResource.
     */
    public static TopDeviceResource toResourceFromEntity(DeviceConsumptionEntity entity) {
        return new TopDeviceResource(
            entity.getDevice().getId(),
            entity.getDevice().getName().name(),
            entity.getDevice().getType().type(),
            entity.getDevice().getCategory().category(),
            entity.getConsumo(),
            entity.getPeriodo()
        );
    }
}