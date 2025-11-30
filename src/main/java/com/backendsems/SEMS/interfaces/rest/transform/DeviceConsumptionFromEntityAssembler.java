package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.interfaces.rest.resources.DeviceConsumptionResource;

/**
 * DeviceConsumptionFromEntityAssembler
 * Ensamblador para convertir DeviceConsumptionEntity a DeviceConsumptionResource.
 */
public class DeviceConsumptionFromEntityAssembler {

    /**
     * Convierte un DeviceConsumptionEntity a DeviceConsumptionResource.
     * @param entity La entidad de consumo.
     * @return DeviceConsumptionResource.
     */
    public static DeviceConsumptionResource toResource(DeviceConsumptionEntity entity) {
        return new DeviceConsumptionResource(
                entity.getId(),
                entity.getDevice().getId(),
                entity.getPeriodo(),
                entity.getConsumo()
        );
    }
}