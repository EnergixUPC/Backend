package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.SEMS.domain.model.entities.SavingRule;
import com.backendsems.SEMS.interfaces.rest.resources.SavingRuleResource;
import com.backendsems.SEMS.interfaces.rest.resources.SettingsResource;

import java.util.List;
import java.util.stream.Collectors;

public class SettingsAssembler {

    public static SettingsResource toResource(UserSetting entity) {
        List<SavingRuleResource> rules = entity.getSavingRules().stream()
                .map(SettingsAssembler::toResource)
                .collect(Collectors.toList());

        return new SettingsResource(
                entity.getId(),
                entity.getUserId().id(),
                entity.isNotificationsEnabled(),
                entity.isHighConsumptionAlerts(),
                entity.isDailyWeeklySummary(),
                entity.getNotificationScheduleStart(),
                entity.getNotificationScheduleEnd(),
                entity.isReportDaily(),
                entity.isReportWeekly(),
                entity.isReportMonthly(),
                entity.isReportFormatPdf(),
                entity.isReportFormatCsv(),
                entity.isTwoFactorEnabled(),
                entity.getLastPasswordChange(),
                rules
        );
    }

    public static SavingRuleResource toResource(SavingRule entity) {
        return new SavingRuleResource(
                entity.getId(),
                entity.getName(),
                entity.isEnabled()
        );
    }
}
