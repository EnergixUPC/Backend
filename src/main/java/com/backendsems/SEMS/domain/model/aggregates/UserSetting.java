package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.entities.SavingRule;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UserSetting
 * Aggregate Root que gestiona las configuraciones globales del usuario.
 */
@Getter
@Entity
@Table(name = "user_settings")
public class UserSetting extends AuditableModel {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", unique = true))
    @NotNull
    private UserId userId;

    // Notifications & Alerts
    @Column(nullable = false)
    private boolean notificationsEnabled;

    @Column(nullable = false)
    private boolean highConsumptionAlerts;

    @Column(nullable = false)
    private boolean dailyWeeklySummary;

    @Column(name = "notification_schedule_start")
    private LocalTime notificationScheduleStart;

    @Column(name = "notification_schedule_end")
    private LocalTime notificationScheduleEnd;

    // Personalized Reports
    @Column(nullable = false)
    private boolean reportDaily;

    @Column(nullable = false)
    private boolean reportWeekly;

    @Column(nullable = false)
    private boolean reportMonthly;

    @Column(nullable = false)
    private boolean reportFormatPdf;

    @Column(nullable = false)
    private boolean reportFormatCsv;

    // Security & Privacy
    @Column(nullable = false)
    private boolean twoFactorEnabled;

    @Column(name = "last_password_change")
    private Date lastPasswordChange;

    @OneToMany(mappedBy = "userSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingRule> savingRules = new ArrayList<>();

    public UserSetting() {
        // Default constructor for JPA
    }

    public UserSetting(UserId userId) {
        this.userId = userId;
        this.notificationsEnabled = true;
        this.highConsumptionAlerts = true;
        this.dailyWeeklySummary = true;
        this.notificationScheduleStart = LocalTime.of(5, 0); // 05:00 AM
        this.notificationScheduleEnd = LocalTime.of(22, 0); // 22:00 PM
        this.reportDaily = true;
        this.reportWeekly = false;
        this.reportMonthly = false;
        this.reportFormatPdf = true;
        this.reportFormatCsv = false;
        this.twoFactorEnabled = false;
    }

    // Business Methods
    public void updateNotificationSettings(boolean enabled, boolean highConsumption, boolean summary, LocalTime start, LocalTime end) {
        this.notificationsEnabled = enabled;
        this.highConsumptionAlerts = highConsumption;
        this.dailyWeeklySummary = summary;
        this.notificationScheduleStart = start;
        this.notificationScheduleEnd = end;
    }

    public void updateReportSettings(boolean daily, boolean weekly, boolean monthly, boolean pdf, boolean csv) {
        this.reportDaily = daily;
        this.reportWeekly = weekly;
        this.reportMonthly = monthly;
        this.reportFormatPdf = pdf;
        this.reportFormatCsv = csv;
    }

    public void updateSecuritySettings(boolean twoFactor) {
        this.twoFactorEnabled = twoFactor;
    }

    public void addSavingRule(SavingRule rule) {
        this.savingRules.add(rule);
        rule.assignUserSetting(this);
    }

    public void removeSavingRule(SavingRule rule) {
        this.savingRules.remove(rule);
    }
}
