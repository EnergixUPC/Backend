package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.DailyConsumption;
import com.backendsems.SEMS.infrastructure.repositories.DailyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/consumption")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsumptionController {
    
    private final DailyConsumptionRepository dailyConsumptionRepository;
    
    @GetMapping("/daily")
    public ResponseEntity<List<DailyConsumption>> getDailyConsumption(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<DailyConsumption> consumption = dailyConsumptionRepository.findByUserIdOrderByDateDesc(userId);
            return ResponseEntity.ok(consumption);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyConsumption> getDailyConsumptionByDate(
            @PathVariable String date,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            LocalDate localDate = LocalDate.parse(date);
            Optional<DailyConsumption> consumption = dailyConsumptionRepository.findByUserIdAndDate(userId, localDate);
            return consumption.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getConsumptionByCategory(Authentication authentication) {
        try {
            // Datos simulados basados en el JSON original
            Map<String, Object> response = Map.of(
                "id", 1,
                "categories", List.of(
                    Map.of("name", "Lighting", "value", 30, "percentage", 30, "color", "#E3F2FD"),
                    Map.of("name", "Heating & Cooling", "value", 25, "percentage", 25, "color", "#1976D2"),
                    Map.of("name", "Electronics", "value", 20, "percentage", 20, "color", "#2196F3"),
                    Map.of("name", "Major Appliances", "value", 15, "percentage", 15, "color", "#64B5F6"),
                    Map.of("name", "Other", "value", 10, "percentage", 10, "color", "#BBDEFB")
                ),
                "totalConsumption", 100
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyComparison(Authentication authentication) {
        try {
            // Datos simulados basados en el JSON original
            Map<String, Object> response = Map.of(
                "id", 1,
                "months", List.of(
                    Map.of("month", "Jun", "year", 2025, "consumption", 180),
                    Map.of("month", "Jul", "year", 2025, "consumption", 220),
                    Map.of("month", "Aug", "year", 2025, "consumption", 195),
                    Map.of("month", "Sep", "year", 2025, "consumption", 250)
                ),
                "currentMonth", "Sep",
                "previousMonthComparison", 15
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}