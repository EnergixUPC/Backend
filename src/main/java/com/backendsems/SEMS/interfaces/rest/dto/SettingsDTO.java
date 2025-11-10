// src/main/java/com/backendsems/SEMS/interfaces/rest/dto/SettingsDTO.java
package com.backendsems.SEMS.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {

    private Long id;
    private Long userId;
    private AutoSavingModeDTO autoSavingMode;
    private NotificationConfigDTO notifications;
    private List<String> reportFrequencies;
    private List<String> reportFormats;
    private Boolean twoFactorEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AutoSavingModeDTO {
        private Boolean turnOffPatio;
        private Boolean turnOffDevices;
        private Boolean unplugWeekdays;
        private Boolean runDishwasher;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationConfigDTO {
        private Boolean highConsumption;
        private Boolean summary;
        private String scheduleStart;
        private String scheduleEnd;
    }
}