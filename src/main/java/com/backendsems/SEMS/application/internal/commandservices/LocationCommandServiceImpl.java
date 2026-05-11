package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.commands.CreateLocationCommand;
import com.backendsems.SEMS.domain.model.entities.Location;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.LocationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.LocationRepository;
import org.springframework.stereotype.Service;

/**
 * LocationCommandServiceImpl
 * Implementacion del servicio de comandos para ubicaciones.
 */
@Service
public class LocationCommandServiceImpl implements LocationCommandService {

    private final LocationRepository locationRepository;

    public LocationCommandServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Long handle(CreateLocationCommand command, UserId userId) {
        Location location = new Location(userId, command.name());
        return locationRepository.save(location).getId();
    }
}
