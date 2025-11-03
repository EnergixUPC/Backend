package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.DashboardStats;
import com.backendsems.SEMS.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardStatsRepository extends JpaRepository<DashboardStats, Long> {
    Optional<DashboardStats> findByUser(User user);
    Optional<DashboardStats> findByUserId(Long userId);
}