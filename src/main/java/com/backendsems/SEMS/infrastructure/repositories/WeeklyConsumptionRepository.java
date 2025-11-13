package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * WeeklyConsumptionRepository
 * Repositorio para el manejo de datos de consumo semanal
 */
@Repository
public interface WeeklyConsumptionRepository extends JpaRepository<WeeklyConsumption, Long> {
    
    List<WeeklyConsumption> findByUserId(Long userId);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId ORDER BY w.startDate DESC")
    List<WeeklyConsumption> findByUserIdOrderByDateDesc(@Param("userId") Long userId);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId AND w.week LIKE :year%")
    List<WeeklyConsumption> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") String year);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId AND w.week = :week")
    Optional<WeeklyConsumption> findByUserIdAndYearAndWeek(@Param("userId") Long userId, 
                                                           @Param("week") String week);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId AND w.startDate >= :startDate AND w.endDate <= :endDate")
    List<WeeklyConsumption> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                      @Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId ORDER BY w.startDate DESC LIMIT :limit")
    List<WeeklyConsumption> findRecentWeeklyConsumption(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    @Query("SELECT AVG(w.totalConsumption) FROM WeeklyConsumption w WHERE w.userId = :userId AND w.week LIKE :year%")
    Double getAverageWeeklyConsumption(@Param("userId") Long userId, @Param("year") String year);
    
    @Query("SELECT SUM(w.totalConsumption) FROM WeeklyConsumption w WHERE w.userId = :userId AND w.week LIKE :year%")
    Double getTotalYearlyConsumption(@Param("userId") Long userId, @Param("year") String year);
    
    @Query("SELECT w FROM WeeklyConsumption w WHERE w.userId = :userId AND w.totalConsumption = " +
           "(SELECT MAX(w2.totalConsumption) FROM WeeklyConsumption w2 WHERE w2.userId = :userId)")
    Optional<WeeklyConsumption> findPeakWeekByUserId(@Param("userId") Long userId);
}