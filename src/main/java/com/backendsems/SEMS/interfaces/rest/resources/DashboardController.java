package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.DashboardStats;
import com.backendsems.SEMS.domain.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats(Authentication authentication) {
        try {
            // Aquí deberíamos obtener el userId del token JWT
            // Por ahora usaremos un valor por defecto
            Long userId = 1L; // TODO: Extraer del JWT
            
            DashboardStats stats = dashboardService.getDashboardStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/stats")
    public ResponseEntity<DashboardStats> updateDashboardStats(
            @RequestBody DashboardStats stats,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            
            DashboardStats updatedStats = dashboardService.updateDashboardStats(userId, stats);
            return ResponseEntity.ok(updatedStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}