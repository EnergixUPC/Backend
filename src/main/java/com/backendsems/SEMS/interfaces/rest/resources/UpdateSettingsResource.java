package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalTime;

public record UpdateSettingsResource(
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
    boolean twoFactorEnabled
) {
}
