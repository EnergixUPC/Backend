package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.*;
import com.backendsems.SEMS.domain.model.aggregates.*;
import com.backendsems.SEMS.domain.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataController {
    
    private final DashboardService dashboardService;
    private final DeviceService deviceService;
    private final NotificationService notificationService;
    private final DevicePreferenceService devicePreferenceService;
    private final ReportsService reportsService;
    
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeUserData(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            
            // Crear datos de ejemplo para el dashboard
            DashboardStats defaultStats = DashboardStats.builder()
                .energyConsumption(250.0)
                .estimatedSavings(-15.0)
                .activeDevices(8)
                .estimatedBill(150.35)
                .todayConsumption(9.5)
                .currency("S/.")
                .build();
            dashboardService.updateDashboardStats(userId, defaultStats);
            
            // Crear dispositivos de ejemplo
            createSampleDevices(userId);
            
            // Crear notificaciones de ejemplo
            createSampleNotifications(userId);
            
            // Crear alertas de ejemplo
            createSampleAlerts(userId);
            
            // Crear datos de consumo semanal de ejemplo
            reportsService.generateSampleWeeklyData(userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User data initialized successfully");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error initializing data: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDataSummary(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("dashboardStats", dashboardService.getDashboardStats(userId));
            summary.put("devices", deviceService.getAllDevicesByUserId(userId));
            summary.put("notifications", notificationService.getAllNotificationsByUserId(userId));
            summary.put("alerts", notificationService.getAllAlertsByUserId(userId));
            summary.put("preferences", devicePreferenceService.getDevicePreferences(userId));
            summary.put("reportsStats", reportsService.getWeeklyConsumptionStats(userId));
            summary.put("recentWeeklyConsumption", reportsService.getRecentWeeklyConsumption(userId, 8));
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private void createSampleDevices(Long userId) {
        Device[] devices = {
            Device.builder()
                .name("Air Conditioner")
                .category("Heating & Cooling")
                .type(Device.DeviceType.AIR_CONDITIONER)
                .status(Device.DeviceStatus.ON)
                .realTimeStatus("On")
                .lastActive("Now")
                .alertHistory("No alerts")
                .energyConsumption("2 kWh this week")
                .location("Living Room")
                .isActive(true)
                .build(),
            
            Device.builder()
                .name("Refrigerator")
                .category("Major Appliances")
                .type(Device.DeviceType.REFRIGERATOR)
                .status(Device.DeviceStatus.ON)
                .realTimeStatus("On")
                .lastActive("Now")
                .alertHistory("No alerts")
                .energyConsumption("30 kWh this week")
                .location("Kitchen")
                .isActive(true)
                .build(),
            
            Device.builder()
                .name("TV")
                .category("Electronics")
                .type(Device.DeviceType.TV)
                .status(Device.DeviceStatus.STANDBY)
                .realTimeStatus("Standby")
                .lastActive("15 minutes ago")
                .alertHistory("Phantom load alert")
                .energyConsumption("8 kWh this week")
                .location("Living Room")
                .isActive(false)
                .build()
        };
        
        for (Device device : devices) {
            deviceService.createDevice(userId, device);
        }
    }
    
    private void createSampleNotifications(Long userId) {
        NotificationAggregate[] notifications = {
            NotificationAggregate.builder()
                .title("Energy savings achieved")
                .message("You saved 15% energy this month compared to last month.")
                .type(NotificationAggregate.NotificationType.ENERGY_SAVING_TIP)
                .status(NotificationAggregate.NotificationStatus.READ)
                .build(),
            
            NotificationAggregate.builder()
                .title("Device maintenance")
                .message("Smart Fan in Master Bedroom needs maintenance.")
                .type(NotificationAggregate.NotificationType.SYSTEM_ALERT)
                .status(NotificationAggregate.NotificationStatus.READ)
                .build(),
            
            NotificationAggregate.builder()
                .title("System update completed")
                .message("Your SEMS dashboard was updated successfully.")
                .type(NotificationAggregate.NotificationType.SYSTEM_ALERT)
                .status(NotificationAggregate.NotificationStatus.READ)
                .build()
        };
        
        for (NotificationAggregate notification : notifications) {
            notificationService.createNotification(userId, notification);
        }
    }
    
    private void createSampleAlerts(Long userId) {
        Alert[] alerts = {
            Alert.builder()
                .type(Alert.AlertType.WARNING)
                .title("High consumption detected!")
                .message("Your usage is 20% above average in the last 3 hours.")
                .icon("warning")
                .isRead(false)
                .build(),
            
            Alert.builder()
                .type(Alert.AlertType.INFO)
                .title("Reminder:")
                .message("You forgot to turn off the patio lights.")
                .icon("info")
                .isRead(false)
                .build()
        };
        
        for (Alert alert : alerts) {
            notificationService.createAlert(userId, alert);
        }
    }
}