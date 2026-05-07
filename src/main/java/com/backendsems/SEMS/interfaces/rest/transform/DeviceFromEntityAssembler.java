package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.interfaces.rest.resources.DeviceResource;

/**
 * DeviceFromEntityAssembler
 * Ensamblador para convertir Device a DeviceResource.
 */
public class DeviceFromEntityAssembler {

    /**
     * Convierte un Device a DeviceResource.
     * @param device El aggregate Device.
     * @return DeviceResource.
     */
    public static DeviceResource toResource(Device device) {
        return new DeviceResource(
                device.getId(),
                String.valueOf(device.getUserId().id()),
                device.getName().name(),
                device.getCategory().category(),
                device.getStatus().status(),
                device.getActivity().activity(),
                device.getLocation().location(),
                device.isActivo()
        );
    }
}