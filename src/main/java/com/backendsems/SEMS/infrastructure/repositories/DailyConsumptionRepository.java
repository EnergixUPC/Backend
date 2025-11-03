package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.DailyConsumption;
import com.backendsems.SEMS.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyConsumptionRepository extends JpaRepository<DailyConsumption, Long> {
    List<DailyConsumption> findByUser(User user);
    List<DailyConsumption> findByUserId(Long userId);
    Optional<DailyConsumption> findByUserIdAndDate(Long userId, LocalDate date);
    List<DailyConsumption> findByUserIdOrderByDateDesc(Long userId);
}