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

    // US23: horario de "hora punta" configurado por el usuario, distinto de la ventana de
    // silencio de notificaciones de arriba. Se usa para distinguir alertas de alto consumo
    // que ocurren en horario de mayor demanda eléctrica.
    @Column(name = "peak_hour_start")
    private LocalTime peakHourStart;

    @Column(name = "peak_hour_end")
    private LocalTime peakHourEnd;

    // US23: umbral de consumo instantáneo (kWh) propio del usuario para disparar alertas.
    // Nullable: si no se configura, se usa el umbral global por defecto del sistema.
    @Column(name = "high_consumption_threshold_kwh")
    private Double highConsumptionThresholdKwh;

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
        this.peakHourStart = LocalTime.of(18, 0); // Horario punta habitual en Perú: 18:00-23:00
        this.peakHourEnd = LocalTime.of(23, 0);
        this.highConsumptionThresholdKwh = null; // null = usa el umbral global por defecto
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

    public void updatePeakHourSettings(LocalTime start, LocalTime end, Double highConsumptionThresholdKwh) {
        this.peakHourStart = start;
        this.peakHourEnd = end;
        this.highConsumptionThresholdKwh = highConsumptionThresholdKwh;
    }

    /** US23: true si la hora dada cae dentro de la ventana de hora punta configurada (soporta ventanas que cruzan medianoche). */
    public boolean isWithinPeakHour(LocalTime time) {
        if (peakHourStart == null || peakHourEnd == null || time == null) return false;
        if (peakHourStart.isBefore(peakHourEnd)) {
            return !time.isBefore(peakHourStart) && time.isBefore(peakHourEnd);
        }
        // Ventana que cruza medianoche, p.ej. 22:00-02:00
        return !time.isBefore(peakHourStart) || time.isBefore(peakHourEnd);
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
