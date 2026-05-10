package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

    List<Consumption> findByDeviceId(String deviceId);

    void deleteByDeviceId(String deviceId);

    List<Consumption> findByDeviceIdInAndCalculatedAtBetween(
            List<String> deviceIds, LocalDateTime start, LocalDateTime end);
}

