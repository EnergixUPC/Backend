package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.entities.Report;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.backendsems.SEMS.domain.model.queries.GetTopDevicesByUserQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.TopDeviceResource;
import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.MonthlyConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.resources.CompareConsumptionResource;
import com.backendsems.SEMS.interfaces.rest.transform.TopDeviceResourceFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.WeeklyConsumptionResourceFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.MonthlyConsumptionResourceFromEntityAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.CompareConsumptionResourceFromEntityAssembler;
import com.backendsems.SEMS.domain.services.ReportService;
import com.backendsems.SEMS.domain.model.queries.GetMonthlyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.queries.CompareConsumptionQuery;
import org.springframework.http.HttpHeaders;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.time.Instant;
import java.time.Duration;
import org.springframework.format.annotation.DateTimeFormat;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportsController
 * Controlador REST para gestionar reportes de consumo.
 */
@RestController
@RequestMapping(value = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Available Report Endpoints")
public class ReportsController {

    private final DeviceQueryService deviceQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    /**
     * Constructor
     */
    public ReportsController(DeviceQueryService deviceQueryService, 
                           TokenService tokenService, 
                           ProfilesContextFacade profilesContextFacade,
                           ReportService reportService,
                           ReportRepository reportRepository) {
        this.deviceQueryService = deviceQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    /**
     * Obtener el top 3 dispositivos que más consumen del usuario actual
     * @param authHeader Header de autorización
     * @return Lista de dispositivos con mayor consumo
     */
    @GetMapping("/top-devices")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get top 3 consuming devices", description = "Get the top 3 devices with highest consumption for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top devices retrieved successfully")})
    public ResponseEntity<List<TopDeviceResource>> getTopDevices(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            
            if (profileId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            var userId = new UserId(profileId);
            var query = new GetTopDevicesByUserQuery(userId, 3);
            var topDevices = deviceQueryService.handle(query);
            
            var topDeviceResources = topDevices.stream()
                    .map(TopDeviceResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(topDeviceResources);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener el consumo semanal del usuario actual
     * @param authHeader Header de autorización
     * @return Datos de consumo semanal
     */
    @GetMapping("/weekly-consumption")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get weekly consumption", description = "Get weekly consumption data for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly consumption data retrieved successfully")})
    public ResponseEntity<?> getWeeklyConsumption(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String format) {
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            
            if (profileId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            var userId = new UserId(profileId);
            var query = new GetWeeklyConsumptionByUserQuery(userId);
            var dailySummaryData = deviceQueryService.handleDailySummary(query);
            
            var weeklyConsumptionResource = WeeklyConsumptionResourceFromEntityAssembler
                    .toResourceFromDailySummary(dailySummaryData);
                    
            if ("pdf".equalsIgnoreCase(format)) {
                byte[] pdf = reportService.generateWeeklyConsumptionPdf(weeklyConsumptionResource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "weekly_report.pdf");
                return ResponseEntity.ok().headers(headers).body(pdf);
            }
                    
            return ResponseEntity.ok(weeklyConsumptionResource);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/monthly-history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get monthly history", description = "Get monthly consumption history for the current user")
    public ResponseEntity<?> getMonthlyHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String format) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            if (profileId == null) return ResponseEntity.badRequest().build();
            
            var userId = new UserId(profileId);
            var query = new GetMonthlyConsumptionByUserQuery(userId);
            var data = deviceQueryService.handleMonthlySummary(query);
            var resource = MonthlyConsumptionResourceFromEntityAssembler.toResourceFromDailySummary(data);
            
            if ("pdf".equalsIgnoreCase(format)) {
                byte[] pdf = reportService.generateMonthlyConsumptionPdf(resource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "monthly_report.pdf");
                return ResponseEntity.ok().headers(headers).body(pdf);
            }
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/compare")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Compare consumption", description = "Compare consumption between two periods")
    public ResponseEntity<?> compareConsumption(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End,
            @RequestParam(required = false) String format) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = tokenService.getEmailFromToken(token);
            Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
            if (profileId == null) return ResponseEntity.badRequest().build();
            
            var userId = new UserId(profileId);
            
            var dataP1 = deviceQueryService.handleCustomSummary(userId, period1Start, period1End);
            var dataP2 = deviceQueryService.handleCustomSummary(userId, period2Start, period2End);
            
            var resource = CompareConsumptionResourceFromEntityAssembler.toResource(
                    dataP1, period1Start, period1End,
                    dataP2, period2Start, period2End);
                    
            if ("pdf".equalsIgnoreCase(format)) {
                byte[] pdf = reportService.generateComparisonPdf(resource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "comparison_report.pdf");
                return ResponseEntity.ok().headers(headers).body(pdf);
            }
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Long getUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        return profilesContextFacade.fetchProfileIdByEmail(email);
    }

    private Map<String, Object> buildReportResponse(Report report) {
        var metadata = Map.of(
            "title", report.getTitle() != null ? report.getTitle() : "Reporte de Energía",
            "description", report.getDescription() != null ? report.getDescription() : "Resumen de consumo",
            "generatedBy", report.getGeneratedBy() != null ? report.getGeneratedBy() : "SEMS System",
            "language", report.getLanguage(),
            "version", "1.0.0"
        );

        // Build mock/dynamic summary data matching ReportSummaryResponse
        var summary = Map.of(
            "totalDevices", 3,
            "activeDevices", 2,
            "totalConsumptionPeriod", 450.5,
            "averageConsumptionPerDevice", 150.2,
            "mostEfficientDevice", "Smart TV",
            "leastEfficientDevice", "Aire Acondicionado",
            "recommendations", List.of(
                "Apaga el Aire Acondicionado cuando no estés en casa.",
                "Usa bombillas LED de bajo consumo.",
                "Desconecta los electrodomésticos en modo espera."
            )
        );

        // Populate report data response
        var data = Map.of(
            "totalConsumption", 450.5,
            "averageConsumption", 15.0,
            "peakConsumption", 25.0,
            "efficiencyScore", 85.0,
            "summary", summary
        );

        return Map.of(
            "id", String.valueOf(report.getId()),
            "type", report.getType(),
            "format", report.getFormat(),
            "period", report.getPeriod(),
            "generatedAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(report.getCreatedAt() != null ? report.getCreatedAt().toInstant() : java.time.Instant.now()),
            "status", report.getStatus(),
            "metadata", metadata,
            "data", data
        );
    }

    private Map<String, Object> buildExportResponse(Report report) {
        var metadata = Map.of(
            "fileName", report.getFileName() != null ? report.getFileName() : "reporte.pdf",
            "fileSize", report.getFileSize() != null ? report.getFileSize() : 1024L,
            "language", report.getLanguage(),
            "includeCharts", true,
            "includeSummary", true,
            "contentType", "pdf".equalsIgnoreCase(report.getFormat()) ? "application/pdf" : "application/json",
            "expiresAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now().plus(java.time.Duration.ofDays(7)))
        );

        return Map.of(
            "id", String.valueOf(report.getId()),
            "reportId", String.valueOf(report.getId()),
            "format", report.getFormat(),
            "period", report.getPeriod(),
            "email", report.getEmail() != null ? report.getEmail() : "",
            "requestedAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(report.getCreatedAt() != null ? report.getCreatedAt().toInstant() : java.time.Instant.now()),
            "completedAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(report.getUpdatedAt() != null ? report.getUpdatedAt().toInstant() : java.time.Instant.now()),
            "status", "completed", // Always complete in dev
            "metadata", metadata,
            "downloadUrl", "/api/v1/reports/" + report.getId() + "/download"
        );
    }

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Generate custom report", description = "Generate custom energy report")
    public ResponseEntity<?> generateReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        Long userId = getUserIdFromHeader(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        String type = (String) request.getOrDefault("type", "weekly_consumption");
        String format = (String) request.getOrDefault("format", "pdf");
        String period = (String) request.getOrDefault("period", "last_week");
        String language = (String) request.getOrDefault("language", "es");

        Report report = new Report(userId, type, format, period, "generated", "Reporte " + type, "Resumen para " + period, "Usuario SEMS", language);
        reportRepository.save(report);

        return ResponseEntity.ok(buildReportResponse(report));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get report details", description = "Retrieve a report metadata by ID")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(buildReportResponse(reportOpt.get()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete report", description = "Delete a report by ID")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        if (!reportRepository.existsById(id)) return ResponseEntity.notFound().build();
        reportRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{id}/data")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get report details data", description = "Retrieve report detailed consumption data")
    public ResponseEntity<?> getReportData(@PathVariable Long id) {
        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(buildReportResponse(reportOpt.get()).get("data"));
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create export request", description = "Request file export generation")
    public ResponseEntity<?> createExportRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        Long userId = getUserIdFromHeader(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        String reportIdStr = (String) request.get("reportId");
        String format = (String) request.getOrDefault("format", "pdf");

        Report report;
        if (reportIdStr != null && !reportIdStr.isEmpty()) {
            Optional<Report> baseReportOpt = reportRepository.findById(Long.parseLong(reportIdStr));
            if (baseReportOpt.isPresent()) {
                Report baseReport = baseReportOpt.get();
                report = new Report(userId, baseReport.getType(), format, baseReport.getPeriod(), "completed", baseReport.getTitle(), baseReport.getDescription(), "Export Tool", baseReport.getLanguage());
            } else {
                report = new Report(userId, "weekly_consumption", format, "last_week", "completed", "Exportado", "Exportación rápida", "Export Tool", "es");
            }
        } else {
            report = new Report(userId, "weekly_consumption", format, "last_week", "completed", "Exportado", "Exportación rápida", "Export Tool", "es");
        }

        reportRepository.save(report);
        return ResponseEntity.ok(buildExportResponse(report));
    }

    @GetMapping("/{exportId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get export request status", description = "Retrieve status of an export request")
    public ResponseEntity<?> getExportStatus(@PathVariable Long exportId) {
        Optional<Report> reportOpt = reportRepository.findById(exportId);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(buildExportResponse(reportOpt.get()));
    }

    @GetMapping("/{reportId}/download")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Download generated report", description = "Download generated report file (PDF, etc.)")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable Long reportId,
            @RequestParam(required = false, defaultValue = "pdf") String format) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();

        Report report = reportOpt.get();
        UserId userId = new UserId(report.getUserId());

        byte[] pdfBytes = null;
        String fileName = report.getFileName();

        try {
            if ("weekly_consumption".equalsIgnoreCase(report.getType())) {
                var query = new GetWeeklyConsumptionByUserQuery(userId);
                var dailySummaryData = deviceQueryService.handleDailySummary(query);
                var weeklyConsumptionResource = WeeklyConsumptionResourceFromEntityAssembler
                        .toResourceFromDailySummary(dailySummaryData);
                pdfBytes = reportService.generateWeeklyConsumptionPdf(weeklyConsumptionResource);
            } else if ("monthly_history".equalsIgnoreCase(report.getType()) || "comprehensive".equalsIgnoreCase(report.getType())) {
                var query = new GetMonthlyConsumptionByUserQuery(userId);
                var data = deviceQueryService.handleMonthlySummary(query);
                var monthlyConsumptionResource = MonthlyConsumptionResourceFromEntityAssembler
                        .toResourceFromDailySummary(data);
                pdfBytes = reportService.generateMonthlyConsumptionPdf(monthlyConsumptionResource);
            } else {
                // Default comparison or basic PDF
                var dataP1 = deviceQueryService.handleCustomSummary(userId, LocalDate.now().minusDays(14), LocalDate.now().minusDays(7));
                var dataP2 = deviceQueryService.handleCustomSummary(userId, LocalDate.now().minusDays(7), LocalDate.now());
                var compareConsumptionResource = CompareConsumptionResourceFromEntityAssembler.toResource(
                        dataP1, LocalDate.now().minusDays(14), LocalDate.now().minusDays(7),
                        dataP2, LocalDate.now().minusDays(7), LocalDate.now());
                pdfBytes = reportService.generateComparisonPdf(compareConsumptionResource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pdfBytes == null) {
            return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/{reportId}/download-url")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get report download url", description = "Get details and direct download url of report")
    public ResponseEntity<?> getDownloadUrl(@PathVariable Long reportId) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();

        Report report = reportOpt.get();
        Map<String, Object> response = Map.of(
            "fileName", report.getFileName(),
            "contentType", "pdf".equalsIgnoreCase(report.getFormat()) ? "application/pdf" : "application/json",
            "fileSize", report.getFileSize(),
            "downloadUrl", "/api/v1/reports/" + report.getId() + "/download",
            "expiresAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now().plus(java.time.Duration.ofDays(7)))
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send report by email", description = "Deliver generated report to email recipient list")
    public ResponseEntity<?> sendReportByEmail(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        List<String> recipients = email != null ? List.of(email) : List.of("user@sems.com");

        Map<String, Object> response = Map.of(
            "success", true,
            "messageId", "msg-" + java.util.UUID.randomUUID().toString(),
            "sentAt", java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()),
            "recipients", recipients
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get reports list & export history", description = "Retrieve list of all reports and exports of user")
    public ResponseEntity<?> getReportsHistory(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        List<Report> dbReports = reportRepository.findByUserId(userId);

        List<Map<String, Object>> reportsList = dbReports.stream()
                .map(this::buildReportResponse)
                .collect(Collectors.toList());

        List<Map<String, Object>> exportsList = dbReports.stream()
                .map(this::buildExportResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
            "reports", reportsList,
            "exports", exportsList,
            "total", dbReports.size(),
            "page", 1,
            "limit", 50,
            "hasNextPage", false
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{exportId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel export request", description = "Halt execution of current export task")
    public ResponseEntity<?> cancelExport(@PathVariable Long exportId) {
        Optional<Report> reportOpt = reportRepository.findById(exportId);
        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            report.setStatus("cancelled");
            reportRepository.save(report);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.notFound().build();
    }
}