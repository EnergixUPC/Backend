// src/main/java/com/backendsems/SEMS/domain/model/entities/UserSettings.java
package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "turn_off_patio")
    private Boolean turnOffPatio = false;

    @Column(name = "turn_off_devices")
    private Boolean turnOffDevices = false;

    @Column(name = "unplug_weekdays")
    private Boolean unplugWeekdays = false;

    @Column(name = "run_dishwasher")
    private Boolean runDishwasher = false;

    @Column(name = "high_consumption")
    private Boolean highConsumption = false;

    @Column(name = "summary")
    private Boolean summary = false;

    @Column(name = "schedule_start")
    private String scheduleStart = "05:00 AM";

    @Column(name = "schedule_end")
    private String scheduleEnd = "22:00 PM";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_settings_id")
    private List<ReportFrequency> reportFrequencies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_settings_id")
    private List<ReportFormat> reportFormats = new ArrayList<>();

    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public UserSettings(Long id, Long userId,
                        Boolean turnOffPatio, Boolean turnOffDevices,
                        Boolean unplugWeekdays, Boolean runDishwasher,
                        Boolean highConsumption, Boolean summary,
                        String scheduleStart, String scheduleEnd,
                        List<ReportFrequency> reportFrequencies, List<ReportFormat> reportFormats,
                        Boolean twoFactorEnabled,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.turnOffPatio = turnOffPatio != null ? turnOffPatio : false;
        this.turnOffDevices = turnOffDevices != null ? turnOffDevices : false;
        this.unplugWeekdays = unplugWeekdays != null ? unplugWeekdays : false;
        this.runDishwasher = runDishwasher != null ? runDishwasher : false;
        this.highConsumption = highConsumption != null ? highConsumption : false;
        this.summary = summary != null ? summary : false;
        this.scheduleStart = scheduleStart != null ? scheduleStart : "05:00 AM";
        this.scheduleEnd = scheduleEnd != null ? scheduleEnd : "22:00 PM";
        this.reportFrequencies = reportFrequencies != null ? reportFrequencies : new ArrayList<>();
        this.reportFormats = reportFormats != null ? reportFormats : new ArrayList<>();
        this.twoFactorEnabled = twoFactorEnabled != null ? twoFactorEnabled : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}