package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.MonthlyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.CompareConsumptionResource;

public interface ReportService {
    byte[] generateWeeklyConsumptionPdf(WeeklyConsumptionResource data);
    byte[] generateMonthlyConsumptionPdf(MonthlyConsumptionResource data);
    byte[] generateComparisonPdf(CompareConsumptionResource data);
}
