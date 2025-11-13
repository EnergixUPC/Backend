package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.infrastructure.repositories.DevicePreferenceRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DevicePreferenceService {
    
    private final DevicePreferenceRepository devicePreferenceRepository;
    private final UserRepository userRepository;
    
    public DevicePreference getDevicePreferences(Long userId) {
        return devicePreferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreferences(userId));
    }
    
    public DevicePreference updateDevicePreferences(Long userId, DevicePreference preferences) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        DevicePreference existingPreferences = devicePreferenceRepository.findByUserId(userId)
            .orElse(new DevicePreference());
        
        existingPreferences.setUser(user);
        existingPreferences.setEnableEnergyMonitoring(preferences.getEnableEnergyMonitoring());
        existingPreferences.setReceiveHighUsageAlerts(preferences.getReceiveHighUsageAlerts());
        existingPreferences.setMonitorHeatingCooling(preferences.getMonitorHeatingCooling());
        existingPreferences.setMonitorMajorAppliances(preferences.getMonitorMajorAppliances());
        existingPreferences.setMonitorElectronics(preferences.getMonitorElectronics());
        existingPreferences.setMonitorKitchenDevices(preferences.getMonitorKitchenDevices());
        existingPreferences.setIncludeOutdoorLighting(preferences.getIncludeOutdoorLighting());
        existingPreferences.setTrackStandbyPower(preferences.getTrackStandbyPower());
        existingPreferences.setDailySummaryEmails(preferences.getDailySummaryEmails());
        existingPreferences.setWeeklyProgressReports(preferences.getWeeklyProgressReports());
        existingPreferences.setSuggestSavingAutomations(preferences.getSuggestSavingAutomations());
        existingPreferences.setAlertsForUnpluggedDevices(preferences.getAlertsForUnpluggedDevices());
        existingPreferences.setLastUpdated(LocalDateTime.now());
        
        return devicePreferenceRepository.save(existingPreferences);
    }
    
    private DevicePreference createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        DevicePreference defaultPreferences = DevicePreference.builder()
            .user(user)
            .enableEnergyMonitoring(true)
            .receiveHighUsageAlerts(true)
            .monitorHeatingCooling(true)
            .monitorMajorAppliances(false)
            .monitorElectronics(true)
            .monitorKitchenDevices(false)
            .includeOutdoorLighting(true)
            .trackStandbyPower(false)
            .dailySummaryEmails(true)
            .weeklyProgressReports(true)
            .suggestSavingAutomations(true)
            .alertsForUnpluggedDevices(true)
            .lastUpdated(LocalDateTime.now())
            .build();
        
        return devicePreferenceRepository.save(defaultPreferences);
    }
}