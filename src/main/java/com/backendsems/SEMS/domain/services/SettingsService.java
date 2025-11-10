// src/main/java/com/backendsems/SEMS/domain/services/SettingsService.java
package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.UserSettings;
import com.backendsems.SEMS.infrastructure.repositories.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettings getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
    }

    @Transactional
    public UserSettings updateSettings(Long userId, UserSettings updatedSettings) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElse(createDefaultSettings(userId));

        // Update Auto Saving Mode
        if (updatedSettings.getTurnOffPatio() != null) {
            settings.setTurnOffPatio(updatedSettings.getTurnOffPatio());
        }
        if (updatedSettings.getTurnOffDevices() != null) {
            settings.setTurnOffDevices(updatedSettings.getTurnOffDevices());
        }
        if (updatedSettings.getUnplugWeekdays() != null) {
            settings.setUnplugWeekdays(updatedSettings.getUnplugWeekdays());
        }
        if (updatedSettings.getRunDishwasher() != null) {
            settings.setRunDishwasher(updatedSettings.getRunDishwasher());
        }

        // Update Notification Config
        if (updatedSettings.getHighConsumption() != null) {
            settings.setHighConsumption(updatedSettings.getHighConsumption());
        }
        if (updatedSettings.getSummary() != null) {
            settings.setSummary(updatedSettings.getSummary());
        }
        if (updatedSettings.getScheduleStart() != null) {
            settings.setScheduleStart(updatedSettings.getScheduleStart());
        }
        if (updatedSettings.getScheduleEnd() != null) {
            settings.setScheduleEnd(updatedSettings.getScheduleEnd());
        }

        // Update Report Settings
        if (updatedSettings.getReportFrequencies() != null) {
            settings.setReportFrequencies(updatedSettings.getReportFrequencies());
        }
        if (updatedSettings.getReportFormats() != null) {
            settings.setReportFormats(updatedSettings.getReportFormats());
        }

        return userSettingsRepository.save(settings);
    }

    @Transactional
    public UserSettings resetToDefaults(Long userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElse(new UserSettings());

        settings.setUserId(userId);
        settings.setTurnOffPatio(false);
        settings.setTurnOffDevices(false);
        settings.setUnplugWeekdays(false);
        settings.setRunDishwasher(false);
        settings.setHighConsumption(false);
        settings.setSummary(false);
        settings.setScheduleStart("00:00 AM");
        settings.setScheduleEnd("23:59 PM");
        settings.setReportFrequencies(List.of());
        settings.setReportFormats(List.of());
        settings.setTwoFactorEnabled(false);

        return userSettingsRepository.save(settings);
    }

    private UserSettings createDefaultSettings(Long userId) {
        return UserSettings.builder()
                .userId(userId)
                .turnOffPatio(false)
                .turnOffDevices(false)
                .unplugWeekdays(false)
                .runDishwasher(false)
                .highConsumption(false)
                .summary(false)
                .scheduleStart("05:00 AM")
                .scheduleEnd("22:00 PM")
                .reportFrequencies(List.of())
                .reportFormats(List.of())
                .twoFactorEnabled(false)
                .build();
    }




}