package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.domain.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            Long userId = 1L; // TODO: Extraer del JWT
            List<Device> devices = deviceService.getAllDevicesByUserId(userId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Device>> getActiveDevices(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<Device> devices = deviceService.getActiveDevicesByUserId(userId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
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
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Device> createDevice(
            @RequestBody Device device,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            Device createdDevice = deviceService.createDevice(userId, device);
            return ResponseEntity.ok(createdDevice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{deviceId}")
    public ResponseEntity<Device> updateDevice(
            @PathVariable Long deviceId,
            @RequestBody Device device) {
        try {
            Device updatedDevice = deviceService.updateDevice(deviceId, device);
            return ResponseEntity.ok(updatedDevice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long deviceId) {
        try {
            deviceService.deleteDevice(deviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{deviceId}/toggle")
    public ResponseEntity<Device> toggleDevice(@PathVariable Long deviceId) {
        try {
            Device device = deviceService.toggleDeviceStatus(deviceId);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Device>> getDevicesByCategory(
            @PathVariable String category,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<Device> devices = deviceService.getDevicesByCategory(userId, category);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}