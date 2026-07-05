package com.backendsems.SEMS.application.internal.outboundservices;

import com.backendsems.SEMS.interfaces.rest.resources.CompareConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.MonthlyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceImplTest {

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl();
    }

    @Test
    void testGenerateWeeklyConsumptionPdf() {
        var data = new WeeklyConsumptionResource(
                List.of(new WeeklyConsumptionResource.DailyConsumptionData(LocalDate.now(), "Monday", 10.5)),
                10.5,
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );

        byte[] pdfBytes = reportService.generateWeeklyConsumptionPdf(data);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testGenerateMonthlyConsumptionPdf() {
        var data = new MonthlyConsumptionResource(
                List.of(new MonthlyConsumptionResource.DailyConsumptionData(LocalDate.now(), 20.0)),
                20.0,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );

        byte[] pdfBytes = reportService.generateMonthlyConsumptionPdf(data);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testGenerateComparisonPdf() {
        var period1 = new CompareConsumptionResource.PeriodConsumptionData(
                LocalDate.now().minusDays(14), LocalDate.now().minusDays(7), 50.0, List.of()
        );
        var period2 = new CompareConsumptionResource.PeriodConsumptionData(
                LocalDate.now().minusDays(7), LocalDate.now(), 40.0, List.of()
        );

        var data = new CompareConsumptionResource(period1, period2, 10.0, 25.0, List.of("Usa bombillas LED de bajo consumo."), "control");

        byte[] pdfBytes = reportService.generateComparisonPdf(data);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}
