// src/main/java/com/backendsems/SEMS/interfaces/rest/mapper/SettingsMapper.java
package com.backendsems.SEMS.interfaces.rest.mapper;

import com.backendsems.SEMS.domain.model.entities.ReportFormat;
import com.backendsems.SEMS.domain.model.entities.ReportFrequency;
import com.backendsems.SEMS.domain.model.entities.UserSettings;
import com.backendsems.SEMS.interfaces.rest.dto.SettingsDTO;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapper {

    public SettingsDTO toDTO(UserSettings entity) {
        if (entity == null) {
            return null;
        }

        return SettingsDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .autoSavingMode(SettingsDTO.AutoSavingModeDTO.builder()
                        .turnOffPatio(entity.getTurnOffPatio())
                        .turnOffDevices(entity.getTurnOffDevices())
                        .unplugWeekdays(entity.getUnplugWeekdays())
                        .runDishwasher(entity.getRunDishwasher())
                        .build())
                .notifications(SettingsDTO.NotificationConfigDTO.builder()
                        .highConsumption(entity.getHighConsumption())
                        .summary(entity.getSummary())
                        .scheduleStart(entity.getScheduleStart())
                        .scheduleEnd(entity.getScheduleEnd())
                        .build())
                .reportFrequencies(entity.getReportFrequencies().stream().map(ReportFrequency::getFrequency).collect(Collectors.toList()))
                .reportFormats(entity.getReportFormats().stream().map(ReportFormat::getFormat).collect(Collectors.toList()))
                .twoFactorEnabled(entity.getTwoFactorEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserSettings toEntity(SettingsDTO dto, UserSettings existing) {
        if (dto == null) {
            return existing;
        }

        if (existing == null) {
            existing = new UserSettings();
            // Solo setear userId si es una entidad nueva
            if (dto.getUserId() != null) {
                existing.setUserId(dto.getUserId());
            }
        }
        // ⬆️ NO sobrescribir userId si existing ya existe

        if (dto.getAutoSavingMode() != null) {
            existing.setTurnOffPatio(dto.getAutoSavingMode().getTurnOffPatio());
            existing.setTurnOffDevices(dto.getAutoSavingMode().getTurnOffDevices());
            existing.setUnplugWeekdays(dto.getAutoSavingMode().getUnplugWeekdays());
            existing.setRunDishwasher(dto.getAutoSavingMode().getRunDishwasher());
        }

        if (dto.getNotifications() != null) {
            existing.setHighConsumption(dto.getNotifications().getHighConsumption());
            existing.setSummary(dto.getNotifications().getSummary());
            existing.setScheduleStart(dto.getNotifications().getScheduleStart());
            existing.setScheduleEnd(dto.getNotifications().getScheduleEnd());
        }

        if (dto.getReportFrequencies() != null) {
            existing.getReportFrequencies().clear();
            existing.getReportFrequencies().addAll(dto.getReportFrequencies().stream().map(f -> ReportFrequency.builder().frequency(f).build()).collect(Collectors.toList()));
        }

        if (dto.getReportFormats() != null) {
            existing.getReportFormats().clear();
            existing.getReportFormats().addAll(dto.getReportFormats().stream().map(f -> ReportFormat.builder().format(f).build()).collect(Collectors.toList()));
        }

        if (dto.getTwoFactorEnabled() != null) {
            existing.setTwoFactorEnabled(dto.getTwoFactorEnabled());
        }

        return existing;
    }
}