package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumptionData;
import com.backendsems.SEMS.domain.services.ReportsService;
import com.backendsems.SEMS.infrastructure.repositories.WeeklyConsumptionRepository;
import com.backendsems.SEMS.interfaces.rest.dto.ReportsDTO;
import com.backendsems.SEMS.interfaces.rest.mapper.ReportsMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportsController
 * Controlador REST para Weekly Consumption Trend - Formato exacto del db.json
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportsController {

    private final ReportsService reportsService;
    private final ReportsMapper reportsMapper;
    private final WeeklyConsumptionRepository weeklyConsumptionRepository;

    /**
     * Endpoint principal para la gráfica Weekly Consumption Trend
     * Retorna datos en el formato exacto del db.json original
     */
    @GetMapping("/weeklyConsumption")
    public ResponseEntity<ReportsDTO.WeeklyConsumptionResponseDTO> getWeeklyConsumption(
            @Parameter(description = "ID del usuario", example = "1") 
            @RequestParam(value = "userId", defaultValue = "1") Long userId) {
        try {
            // Obtener la semana más reciente
            List<WeeklyConsumption> weeklyConsumptions = reportsService.getRecentWeeklyConsumption(userId, 1);
            
            if (weeklyConsumptions.isEmpty()) {
                // Si no hay datos, generar datos de ejemplo
                reportsService.generateSampleWeeklyData(userId);
                weeklyConsumptions = reportsService.getRecentWeeklyConsumption(userId, 1);
            }
            
            if (!weeklyConsumptions.isEmpty()) {
                WeeklyConsumption weeklyConsumption = weeklyConsumptions.get(0);
                ReportsDTO.WeeklyConsumptionResponseDTO response = reportsMapper.toWeeklyConsumptionResponseDTO(weeklyConsumption);
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generar datos de ejemplo para testing
     * Retorna la misma estructura que el GET principal
     */
    @PostMapping("/weeklyConsumption/generate-sample")
    public ResponseEntity<ReportsDTO.WeeklyConsumptionResponseDTO> generateSampleData(
            @RequestBody ReportsDTO.GenerateSampleDataRequestDTO request) {
        try {
            // Usar ID 1 por defecto para generar datos de ejemplo
            Long userId = 1L;
            System.out.println("DEBUG: Iniciando generateSampleData para userId: " + userId);
            
            // Crear WeeklyConsumption con los datos del request
            WeeklyConsumption weeklyConsumption = new WeeklyConsumption();
            weeklyConsumption.setUserId(userId);
            
            // Usar los datos del request si están disponibles, sino usar valores por defecto
            if (request.getWeek() != null && !request.getWeek().equals("string")) {
                weeklyConsumption.setWeek(request.getWeek());
            } else {
                // Generar semana actual por defecto
                LocalDate now = LocalDate.now();
                int weekNumber = now.get(java.time.temporal.WeekFields.ISO.weekOfYear());
                weeklyConsumption.setWeek(String.format("%d-W%02d", now.getYear(), weekNumber));
            }
            
            // Configurar fechas de inicio y fin
            if (request.getStartDate() != null && !request.getStartDate().equals("string")) {
                weeklyConsumption.setStartDate(LocalDate.parse(request.getStartDate()));
            } else {
                weeklyConsumption.setStartDate(LocalDate.now().with(java.time.DayOfWeek.MONDAY));
            }
            
            if (request.getEndDate() != null && !request.getEndDate().equals("string")) {
                weeklyConsumption.setEndDate(LocalDate.parse(request.getEndDate()));
            } else {
                weeklyConsumption.setEndDate(weeklyConsumption.getStartDate().plusDays(6));
            }
            
            // Configurar estadísticas del request
            weeklyConsumption.setTotalConsumption(request.getTotalConsumption() != null ? request.getTotalConsumption() : 0.1);
            weeklyConsumption.setAverageConsumption(request.getAverageConsumption() != null ? request.getAverageConsumption() : 0.1);
            weeklyConsumption.setPeakDay(request.getPeakDay() != null && !request.getPeakDay().equals("string") ? request.getPeakDay() : "MONDAY");
            weeklyConsumption.setPeakConsumption(request.getPeakConsumption() != null ? request.getPeakConsumption() : 0.1);
            weeklyConsumption.setWeeklyAverage(request.getWeeklyAverage() != null ? request.getWeeklyAverage() : 0.1);
            
            // Configurar dataPoints del request
            weeklyConsumption.setDataPoints(new ArrayList<>());
            if (request.getDataPoints() != null && !request.getDataPoints().isEmpty()) {
                for (ReportsDTO.WeeklyConsumptionResponseDTO.DataPointDTO dataPointDTO : request.getDataPoints()) {
                    WeeklyConsumptionData dataPoint = new WeeklyConsumptionData();
                    dataPoint.setDay(dataPointDTO.getDay() != null && !dataPointDTO.getDay().equals("string") ? dataPointDTO.getDay() : "MONDAY");
                    dataPoint.setDate(dataPointDTO.getDate() != null && !dataPointDTO.getDate().equals("string") ? 
                                    LocalDate.parse(dataPointDTO.getDate()) : LocalDate.now());
                    dataPoint.setConsumption(dataPointDTO.getConsumption() != null ? dataPointDTO.getConsumption() : 0.1);
                    dataPoint.setEfficiency(dataPointDTO.getEfficiency() != null ? dataPointDTO.getEfficiency() : 0);
                    dataPoint.setTrend(dataPointDTO.getTrend() != null && !dataPointDTO.getTrend().equals("string") ? dataPointDTO.getTrend() : "stable");
                    dataPoint.setWeeklyConsumption(weeklyConsumption);
                    weeklyConsumption.getDataPoints().add(dataPoint);
                }
            } else {
                // Si no hay dataPoints en el request, crear uno por defecto
                WeeklyConsumptionData dataPoint = new WeeklyConsumptionData();
                dataPoint.setDay("MONDAY");
                dataPoint.setDate(weeklyConsumption.getStartDate());
                dataPoint.setConsumption(0.1);
                dataPoint.setEfficiency(0);
                dataPoint.setTrend("stable");
                dataPoint.setWeeklyConsumption(weeklyConsumption);
                weeklyConsumption.getDataPoints().add(dataPoint);
            }
            
            // Guardar en base de datos
            WeeklyConsumption savedConsumption = weeklyConsumptionRepository.save(weeklyConsumption);
            System.out.println("DEBUG: WeeklyConsumption guardado con ID: " + savedConsumption.getId());
            
            // Mapear a DTO para respuesta
            ReportsDTO.WeeklyConsumptionResponseDTO response = reportsMapper.toWeeklyConsumptionResponseDTO(savedConsumption);
            System.out.println("DEBUG: Response DTO generado exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR en generateSampleData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}