package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * DeviceConsumptionRepository
 * Repositorio JPA para DeviceConsumptionEntity.
 */
@Repository
public interface DeviceConsumptionRepository extends JpaRepository<DeviceConsumptionEntity, Long> {

    /**
     * Encuentra consumos por deviceId.
     * @param deviceId El ID del dispositivo.
     * @return Lista de consumos.
     */
    List<DeviceConsumptionEntity> findByDeviceId(Long deviceId);

    /**
     * Elimina consumos por deviceId.
     * @param deviceId El ID del dispositivo.
     */
    void deleteByDeviceId(Long deviceId);

    /**
     * Obtiene los dispositivos con mayor consumo diario para un usuario específico.
     * @param userId El ID del usuario.
     * @param period El período de consumo (daily, weekly, monthly).
     * @param pageable Configuración de paginación para limitar resultados.
     * @return Lista de consumos ordenados por mayor consumo.
     */
    @Query("SELECT dc FROM DeviceConsumptionEntity dc " +
           "JOIN dc.device d " +
           "WHERE d.userId = :userId AND dc.periodo = :period " +
           "ORDER BY dc.consumo DESC")
    List<DeviceConsumptionEntity> findTopConsumptionByUserIdAndPeriod(
        @Param("userId") UserId userId, 
        @Param("period") String period, 
        Pageable pageable
    );

    /**
     * Obtiene todos los consumos semanales para un usuario específico.
     * @param userId El ID del usuario.
     * @return Lista de consumos semanales del usuario.
     */
    @Query("SELECT dc FROM DeviceConsumptionEntity dc " +
           "JOIN dc.device d " +
           "WHERE d.userId = :userId AND dc.periodo = 'weekly'")
    List<DeviceConsumptionEntity> findWeeklyConsumptionByUserId(@Param("userId") UserId userId);

    /**
     * Obtiene todos los consumos diarios para un usuario específico.
     * @param userId El ID del usuario.
     * @return Lista de consumos diarios del usuario.
     */
    @Query("SELECT dc FROM DeviceConsumptionEntity dc " +
           "JOIN dc.device d " +
           "WHERE d.userId = :userId AND dc.periodo = 'daily'")
    List<DeviceConsumptionEntity> findDailyConsumptionByUserId(@Param("userId") UserId userId);

    /**
     * Obtiene los consumos diarios de un usuario en un rango de fechas específico.
     * @param userId El ID del usuario.
     * @param startDate Fecha de inicio (inclusive).
     * @param endDate Fecha de fin (inclusive).
     * @return Lista de consumos diarios en el rango de fechas.
     */
    @Query("SELECT dc FROM DeviceConsumptionEntity dc " +
           "JOIN dc.device d " +
           "WHERE d.userId = :userId AND dc.periodo = 'daily' " +
           "AND dc.fecha IS NOT NULL AND dc.fecha BETWEEN :startDate AND :endDate " +
           "ORDER BY dc.fecha ASC")
    List<DeviceConsumptionEntity> findDailyConsumptionByUserIdAndDateRange(
        @Param("userId") UserId userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Obtiene el consumo total por día para un usuario en un rango de fechas.
     * @param userId El ID del usuario.
     * @param startDate Fecha de inicio (inclusive).
     * @param endDate Fecha de fin (inclusive).
     * @return Lista de objetos con fecha y suma de consumo por día.
     */
    @Query("SELECT dc.fecha, SUM(dc.consumo) " +
           "FROM DeviceConsumptionEntity dc " +
           "JOIN dc.device d " +
           "WHERE d.userId = :userId AND dc.periodo = 'daily' " +
           "AND dc.fecha IS NOT NULL AND dc.fecha BETWEEN :startDate AND :endDate " +
           "GROUP BY dc.fecha " +
           "ORDER BY dc.fecha ASC")
    List<Object[]> findDailyConsumptionSumByUserIdAndDateRange(
        @Param("userId") UserId userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}