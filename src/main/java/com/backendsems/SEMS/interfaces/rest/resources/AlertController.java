package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.Alert;
import com.backendsems.SEMS.domain.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<Alert> alerts = notificationService.getAllAlertsByUserId(userId);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<Alert>> getUnreadAlerts(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<Alert> alerts = notificationService.getUnreadAlertsByUserId(userId);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Alert> createAlert(
            @RequestBody Alert alert,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            Alert createdAlert = notificationService.createAlert(userId, alert);
            return ResponseEntity.ok(createdAlert);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{alertId}/read")
    public ResponseEntity<Alert> markAsRead(@PathVariable Long alertId) {
        try {
            Alert alert = notificationService.markAlertAsRead(alertId);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        try {
            notificationService.deleteAlert(alertId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/count/unread")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            Long count = notificationService.getUnreadAlertCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}