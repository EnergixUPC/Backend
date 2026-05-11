package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.Location;
import com.backendsems.SEMS.interfaces.rest.resources.LocationResource;

/**
 * LocationFromEntityAssembler
 * Ensamblador para convertir Location a LocationResource.
 */
public class LocationFromEntityAssembler {

    /**
     * Convierte una Location a LocationResource.
     * @param location La entidad Location.
     * @return LocationResource.
     */
    public static LocationResource toResource(Location location) {
        return new LocationResource(
                location.getId(),
                location.getName(),
                String.valueOf(location.getUserId().id())
        );
    }
}
