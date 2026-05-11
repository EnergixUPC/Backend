package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.Location;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * LocationRepository
 * Repositorio JPA para Location.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Encuentra ubicaciones por userId.
     * @param userId El ID del usuario.
     * @return Lista de ubicaciones.
     */
    List<Location> findByUserId(UserId userId);
}
