package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.interfaces.rest.resources.DeviceConsumptionResource;

/**
 * DeviceConsumptionFromEntityAssembler
 * Ensamblador para convertir DeviceConsumption a DeviceConsumptionResource.
 */
public class DeviceConsumptionFromEntityAssembler {

    /**
     * Convierte un DeviceConsumption a DeviceConsumptionResource.
     * @param entity La entidad de consumo.
     * @return DeviceConsumptionResource.
     */
    public static DeviceConsumptionResource toResource(DeviceConsumption entity) {
        return new DeviceConsumptionResource(
                entity.getId(),
                entity.getDevice().getId(),
                entity.getPeriodo(),
                entity.getConsumo()
        );
    }
}