package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public record SettingsResource(
    Long id,
    Long userId,
    boolean notificationsEnabled,
    boolean highConsumptionAlerts,
    boolean dailyWeeklySummary,
    LocalTime notificationScheduleStart,
    LocalTime notificationScheduleEnd,
    boolean reportDaily,
    boolean reportWeekly,
    boolean reportMonthly,
    boolean reportFormatPdf,
    boolean reportFormatCsv,
    boolean twoFactorEnabled,
    Date lastPasswordChange,
    List<SavingRuleResource> savingRules
) {
}
