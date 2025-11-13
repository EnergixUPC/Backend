package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import com.backendsems.SEMS.domain.services.ReportsService;
import com.backendsems.SEMS.interfaces.rest.dto.ReportsDTO;
import com.backendsems.SEMS.interfaces.rest.mapper.ReportsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Endpoint principal para la gráfica Weekly Consumption Trend
     * Retorna datos en el formato exacto del db.json original
     */
    @GetMapping("/weeklyConsumption")
    public ResponseEntity<ReportsDTO.WeeklyConsumptionResponseDTO> getWeeklyConsumption(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            
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
    public ResponseEntity<ReportsDTO.WeeklyConsumptionResponseDTO> generateSampleData(Authentication authentication) {
        try {
            System.out.println("DEBUG: Iniciando generateSampleData");
            Long userId = 1L; // TODO: Extraer del JWT
            
            // Generar datos de ejemplo
            System.out.println("DEBUG: Llamando a generateSampleWeeklyData");
            reportsService.generateSampleWeeklyData(userId);
            System.out.println("DEBUG: generateSampleWeeklyData completado");
            
            // Obtener los datos recién generados
            System.out.println("DEBUG: Obteniendo datos recientes");
            List<WeeklyConsumption> weeklyConsumptions = reportsService.getRecentWeeklyConsumption(userId, 1);
            System.out.println("DEBUG: Datos obtenidos: " + weeklyConsumptions.size());
            
            if (!weeklyConsumptions.isEmpty()) {
                WeeklyConsumption weeklyConsumption = weeklyConsumptions.get(0);
                ReportsDTO.WeeklyConsumptionResponseDTO response = reportsMapper.toWeeklyConsumptionResponseDTO(weeklyConsumption);
                System.out.println("DEBUG: Respuesta generada exitosamente");
                return ResponseEntity.ok(response);
            }
            
            System.out.println("DEBUG: No se encontraron datos generados");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("ERROR en generateSampleData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}