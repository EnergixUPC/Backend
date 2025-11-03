package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.services.DevicePreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DevicePreferenceController {
    
    private final DevicePreferenceService devicePreferenceService;
    
    @GetMapping("/devices")
    public ResponseEntity<DevicePreference> getDevicePreferences(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            DevicePreference preferences = devicePreferenceService.getDevicePreferences(userId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/devices")
    public ResponseEntity<DevicePreference> updateDevicePreferences(
            @RequestBody DevicePreference preferences,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            DevicePreference updatedPreferences = devicePreferenceService.updateDevicePreferences(userId, preferences);
            return ResponseEntity.ok(updatedPreferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}