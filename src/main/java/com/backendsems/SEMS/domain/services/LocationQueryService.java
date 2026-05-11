package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Location;
import com.backendsems.SEMS.domain.model.queries.GetLocationsByUserIdQuery;
import java.util.List;

/**
 * LocationQueryService
 * Servicio de queries para ubicaciones.
 */
public interface LocationQueryService {

    /**
     * Maneja la query para obtener ubicaciones por userId.
     * @param query La query GetLocationsByUserIdQuery.
     * @return Lista de ubicaciones.
     */
    List<Location> handle(GetLocationsByUserIdQuery query);
}
