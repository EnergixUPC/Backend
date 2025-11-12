package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.domain.services.DeviceService;
import com.backendsems.SEMS.interfaces.rest.dto.CreateDeviceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            List<Device> devices = deviceService.getAllDevicesByUserId(userId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            System.err.println("Error getting all devices: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Device>> getActiveDevices(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            List<Device> devices = deviceService.getActiveDevicesByUserId(userId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            System.err.println("Error getting active devices: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{deviceId}")
    public ResponseEntity<Device> getDevice(@PathVariable Long deviceId) {
        try {
            Optional<Device> device = deviceService.getDeviceById(deviceId);
            return device.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error getting device: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDevice(
            @RequestBody CreateDeviceDto deviceDto,
            Authentication authentication) {
        try {
            System.out.println("========================================");
            System.out.println("CREATE DEVICE ATTEMPT");
            System.out.println("Device name: " + deviceDto.getName());
            System.out.println("Device type: " + deviceDto.getType());
            System.out.println("Device category: " + deviceDto.getCategory());
            System.out.println("Device location: " + deviceDto.getLocation());
            System.out.println("========================================");

            Long userId = getUserIdFromAuth(authentication);
            Device createdDevice = deviceService.createDevice(userId, deviceDto);
            
            System.out.println("DEVICE CREATED SUCCESSFULLY!");
            System.out.println("Device ID: " + createdDevice.getId());
            
            return ResponseEntity.ok(createdDevice);
        } catch (Exception e) {
            System.err.println("Error creating device: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PutMapping("/{deviceId}")
    public ResponseEntity<?> updateDevice(
            @PathVariable Long deviceId,
            @RequestBody Device device) {
        try {
            Device updatedDevice = deviceService.updateDevice(deviceId, device);
            return ResponseEntity.ok(updatedDevice);
        } catch (Exception e) {
            System.err.println("Error updating device: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long deviceId) {
        try {
            deviceService.deleteDevice(deviceId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Device deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error deleting device: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/{deviceId}/toggle")
    public ResponseEntity<?> toggleDevice(@PathVariable Long deviceId) {
        try {
            Device device = deviceService.toggleDeviceStatus(deviceId);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            System.err.println("Error toggling device: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Device>> getDevicesByCategory(
            @PathVariable String category,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            List<Device> devices = deviceService.getDevicesByCategory(userId, category);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            System.err.println("Error getting devices by category: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Método auxiliar para extraer userId del authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        // Por ahora usamos un userId fijo para testing
        // TODO: Implementar extracción real del JWT
        if (authentication != null && authentication.getName() != null) {
            // Aquí deberías extraer el userId del token JWT
            // Por ahora retornamos un ID fijo para testing
            return 1L;
        }
        return 1L; // Default para testing
    }
}