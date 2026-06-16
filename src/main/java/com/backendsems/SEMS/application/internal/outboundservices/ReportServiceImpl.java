package com.backendsems.SEMS.application.internal.outboundservices;

import com.backendsems.SEMS.domain.services.ReportService;
import com.backendsems.SEMS.interfaces.rest.resources.CompareConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.MonthlyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public byte[] generateWeeklyConsumptionPdf(WeeklyConsumptionResource data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Weekly Consumption Report"));
            document.add(new Paragraph("From: " + data.weekStartDate() + " To: " + data.weekEndDate()));
            document.add(new Paragraph("Total Consumption: " + data.totalWeeklyConsumption()));
            document.add(new Paragraph(" "));

            for (WeeklyConsumptionResource.DailyConsumptionData daily : data.dailyConsumptions()) {
                document.add(new Paragraph(daily.date() + " (" + daily.dayName() + "): " + daily.consumption()));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating weekly PDF report", e);
        }
    }

    @Override
    public byte[] generateMonthlyConsumptionPdf(MonthlyConsumptionResource data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Monthly Consumption Report"));
            document.add(new Paragraph("From: " + data.monthStartDate() + " To: " + data.monthEndDate()));
            document.add(new Paragraph("Total Consumption: " + data.totalMonthlyConsumption()));
            document.add(new Paragraph(" "));

            for (MonthlyConsumptionResource.DailyConsumptionData daily : data.dailyConsumptions()) {
                document.add(new Paragraph(daily.date() + ": " + daily.consumption()));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating monthly PDF report", e);
        }
    }

    @Override
    public byte[] generateComparisonPdf(CompareConsumptionResource data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Comparison Report"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Period 1: " + data.period1().startDate() + " to " + data.period1().endDate()));
            document.add(new Paragraph("Total Consumption: " + data.period1().totalConsumption()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Period 2: " + data.period2().startDate() + " to " + data.period2().endDate()));
            document.add(new Paragraph("Total Consumption: " + data.period2().totalConsumption()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Difference: " + data.difference()));
            document.add(new Paragraph("Percentage Difference: " + String.format("%.2f", data.percentageDifference()) + "%"));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating comparison PDF report", e);
        }
    }
}
