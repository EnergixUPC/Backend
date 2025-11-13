package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeviceRepository
 * Repositorio para la entidad Device
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    List<Device> findByUserId(Long userId);
    
    @Query("SELECT d FROM Device d WHERE d.isActive = true AND d.userId = :userId")
    List<Device> findByUserIdAndIsActive(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.userId = :userId AND d.isActive = true")
    Integer countActiveDevicesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM Device d WHERE d.userId = :userId AND d.type = :type")
    List<Device> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Device.DeviceType type);
    
    @Query("SELECT d FROM Device d WHERE d.isActive = true")
    List<Device> findActiveDevices();
    
    @Query("SELECT d FROM Device d WHERE d.userId = :userId AND d.category = :category")
    List<Device> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
}