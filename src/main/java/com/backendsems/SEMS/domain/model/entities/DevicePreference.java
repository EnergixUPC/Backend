package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DevicePreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "enable_energy_monitoring")
    private Boolean enableEnergyMonitoring;
    
    @Column(name = "receive_high_usage_alerts")
    private Boolean receiveHighUsageAlerts;
    
    @Column(name = "monitor_heating_cooling")
    private Boolean monitorHeatingCooling;
    
    @Column(name = "monitor_major_appliances")
    private Boolean monitorMajorAppliances;
    
    @Column(name = "monitor_electronics")
    private Boolean monitorElectronics;
    
    @Column(name = "monitor_kitchen_devices")
    private Boolean monitorKitchenDevices;
    
    @Column(name = "include_outdoor_lighting")
    private Boolean includeOutdoorLighting;
    
    @Column(name = "track_standby_power")
    private Boolean trackStandbyPower;
    
    @Column(name = "daily_summary_emails")
    private Boolean dailySummaryEmails;
    
    @Column(name = "weekly_progress_reports")
    private Boolean weeklyProgressReports;
    
    @Column(name = "suggest_saving_automations")
    private Boolean suggestSavingAutomations;
    
    @Column(name = "alerts_for_unplugged_devices")
    private Boolean alertsForUnpluggedDevices;
    
    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}