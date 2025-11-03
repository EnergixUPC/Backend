package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.DashboardStats;
import com.backendsems.SEMS.infrastructure.repositories.DashboardStatsRepository;
import com.backendsems.SEMS.infrastructure.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final DashboardStatsRepository dashboardStatsRepository;
    private final DeviceRepository deviceRepository;
    
    public DashboardStats getDashboardStats(Long userId) {
        return dashboardStatsRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultDashboardStats(userId));
    }
    
    public DashboardStats updateDashboardStats(Long userId, DashboardStats stats) {
        DashboardStats existingStats = dashboardStatsRepository.findByUserId(userId)
            .orElse(new DashboardStats());
        
        existingStats.setEnergyConsumption(stats.getEnergyConsumption());
        existingStats.setEstimatedSavings(stats.getEstimatedSavings());
        existingStats.setActiveDevices(deviceRepository.countActiveDevicesByUserId(userId));
        existingStats.setEstimatedBill(stats.getEstimatedBill());
        existingStats.setTodayConsumption(stats.getTodayConsumption());
        existingStats.setCurrency(stats.getCurrency() != null ? stats.getCurrency() : "S/.");
        
        return dashboardStatsRepository.save(existingStats);
    }
    
    private DashboardStats createDefaultDashboardStats(Long userId) {
        Integer activeDevices = deviceRepository.countActiveDevicesByUserId(userId);
        
        DashboardStats defaultStats = DashboardStats.builder()
            .energyConsumption(250.0)
            .estimatedSavings(-15.0)
            .activeDevices(activeDevices != null ? activeDevices : 0)
            .estimatedBill(150.35)
            .todayConsumption(9.5)
            .currency("S/.")
            .build();
        
        return dashboardStatsRepository.save(defaultStats);
    }
}