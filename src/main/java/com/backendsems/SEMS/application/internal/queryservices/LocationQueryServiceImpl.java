package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.entities.Location;
import com.backendsems.SEMS.domain.model.queries.GetLocationsByUserIdQuery;
import com.backendsems.SEMS.domain.services.LocationQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.LocationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * LocationQueryServiceImpl
 * Implementacion del servicio de queries para ubicaciones.
 */
@Service
public class LocationQueryServiceImpl implements LocationQueryService {

    private final LocationRepository locationRepository;

    public LocationQueryServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> handle(GetLocationsByUserIdQuery query) {
        return locationRepository.findByUserId(query.userId());
    }
}
