// src/main/java/com/backendsems/SEMS/interfaces/rest/resources/SettingsController.java
package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.UserSettings;
import com.backendsems.SEMS.domain.services.SettingsService;
import com.backendsems.SEMS.interfaces.rest.dto.SettingsDTO;
import com.backendsems.SEMS.interfaces.rest.mapper.SettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SettingsController {

    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper; // ⬅️ AGREGAR

    @GetMapping("/{userId}")
    public ResponseEntity<SettingsDTO> getUserSettings(@PathVariable Long userId) {
        try {
            UserSettings settings = settingsService.getUserSettings(userId);

            // ⬇️ CONVERTIR A DTO
            SettingsDTO dto = settingsMapper.toDTO(settings);

            System.out.println("🔵 Returning DTO to frontend:");
            System.out.println("   - AutoSavingMode: " + dto.getAutoSavingMode());
            System.out.println("   - Notifications: " + dto.getNotifications());
            System.out.println("   - Frequencies: " + dto.getReportFrequencies());
            System.out.println("   - Formats: " + dto.getReportFormats());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("❌ Error getting settings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<SettingsDTO> updateSettings(
            @PathVariable Long userId,
            @RequestBody SettingsDTO dto) {
        try {
            System.out.println("📝 Received DTO from frontend:");
            System.out.println("   - AutoSavingMode: " + dto.getAutoSavingMode());
            System.out.println("   - Notifications: " + dto.getNotifications());
            System.out.println("   - Frequencies: " + dto.getReportFrequencies());
            System.out.println("   - Formats: " + dto.getReportFormats());

            UserSettings existing = settingsService.getUserSettings(userId);
            UserSettings updated = settingsMapper.toEntity(dto, existing);
            UserSettings saved = settingsService.updateSettings(userId, updated);

            SettingsDTO responseDto = settingsMapper.toDTO(saved);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("❌ Error updating settings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<SettingsDTO> resetToDefaults(@PathVariable Long userId) {
        try {
            UserSettings reset = settingsService.resetToDefaults(userId);
            SettingsDTO dto = settingsMapper.toDTO(reset);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestBody PasswordChangeRequest request) {
        try {
            System.out.println("Password change requested for user: " + userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/2fa/enable")
    public ResponseEntity<TwoFactorResponse> enableTwoFactor(@PathVariable Long userId) {
        try {
            TwoFactorResponse response = new TwoFactorResponse(
                    "https://via.placeholder.com/200x200?text=QR+Code",
                    "MOCK-SECRET-KEY-12345"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

record PasswordChangeRequest(String oldPassword, String newPassword) {}
record TwoFactorResponse(String qrCode, String secret) {}