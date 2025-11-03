package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;
import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeviceRepository
 * Repositorio para el agregado de Dispositivo
 */
@Repository
public interface DeviceRepository extends JpaRepository<DeviceAggregate, Long> {
    
    List<DeviceAggregate> findByUser(UserAggregate user);
    
    @Query("SELECT d FROM DeviceAggregate d WHERE d.user.id = :userId")
    List<DeviceAggregate> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM DeviceAggregate d WHERE d.status.active = true AND d.user.id = :userId")
    List<DeviceAggregate> findByUserIdAndIsActive(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM DeviceAggregate d WHERE d.user.id = :userId AND d.status.active = true")
    Integer countActiveDevicesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM DeviceAggregate d WHERE d.user.id = :userId AND d.type = :type")
    List<DeviceAggregate> findByUserIdAndType(@Param("userId") Long userId, @Param("type") DeviceAggregate.DeviceType type);
    
    @Query("SELECT d FROM DeviceAggregate d WHERE d.status.active = true")
    List<DeviceAggregate> findActiveDevices();
    
    @Query("SELECT d FROM DeviceAggregate d JOIN FETCH d.consumptionReadings WHERE d.id = :deviceId")
    DeviceAggregate findByIdWithConsumptionReadings(@Param("deviceId") Long deviceId);
}