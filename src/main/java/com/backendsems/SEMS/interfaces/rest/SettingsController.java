package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.SEMS.domain.model.entities.SavingRule;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.SettingsRepository;
import com.backendsems.SEMS.interfaces.rest.resources.*;
import com.backendsems.SEMS.interfaces.rest.transform.SettingsAssembler;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.interfaces.rest.resources.ChangePasswordResource;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * SettingsController
 * Controlador REST para gestionar configuraciones de usuario.
 */
@RestController
@RequestMapping(value = "/api/v1/settings", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Settings", description = "User Settings Endpoints")
public class SettingsController {

    private final SettingsRepository settingsRepository;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;
    private final UserRepository userRepository;
    private final HashingService hashingService;

    public SettingsController(SettingsRepository settingsRepository, TokenService tokenService, ProfilesContextFacade profilesContextFacade, UserRepository userRepository, HashingService hashingService) {
        this.settingsRepository = settingsRepository;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    private UserId getUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return null;
        return new UserId(profileId);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user settings", description = "Get settings for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings found"),
            @ApiResponse(responseCode = "404", description = "Settings not found")})
    public ResponseEntity<SettingsResource> getSettings(@RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settings = settingsRepository.findByUserId(userId);
        
        // If settings don't exist, create default ones
        if (settings.isEmpty()) {
            UserSetting newSettings = new UserSetting(userId);
            settingsRepository.save(newSettings);
            return ResponseEntity.ok(SettingsAssembler.toResource(newSettings));
        }

        return ResponseEntity.ok(SettingsAssembler.toResource(settings.get()));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user settings", description = "Update settings for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings updated"),
            @ApiResponse(responseCode = "404", description = "Settings not found")})
    public ResponseEntity<SettingsResource> updateSettings(@RequestBody UpdateSettingsResource resource, @RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty()) return ResponseEntity.notFound().build();

        UserSetting settings = settingsOpt.get();
        settings.updateNotificationSettings(
                resource.notificationsEnabled(),
                resource.highConsumptionAlerts(),
                resource.dailyWeeklySummary(),
                resource.notificationScheduleStart(),
                resource.notificationScheduleEnd()
        );
        settings.updateReportSettings(
                resource.reportDaily(),
                resource.reportWeekly(),
                resource.reportMonthly(),
                resource.reportFormatPdf(),
                resource.reportFormatCsv()
        );
        settings.updateSecuritySettings(resource.twoFactorEnabled());

        settingsRepository.save(settings);
        return ResponseEntity.ok(SettingsAssembler.toResource(settings));
    }

    @PostMapping("/rules")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add saving rule", description = "Add a new saving rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule added"),
            @ApiResponse(responseCode = "404", description = "Settings not found")})
    public ResponseEntity<SettingsResource> addSavingRule(@RequestBody CreateSavingRuleResource resource, @RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty()) return ResponseEntity.notFound().build();

        UserSetting settings = settingsOpt.get();
        SavingRule rule = new SavingRule(resource.name(), resource.isEnabled());
        settings.addSavingRule(rule);
        
        settingsRepository.save(settings);
        return ResponseEntity.ok(SettingsAssembler.toResource(settings));
    }

    @PutMapping("/rules/{ruleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update saving rule", description = "Update an existing saving rule")
    public ResponseEntity<SettingsResource> updateSavingRule(@PathVariable Long ruleId, @RequestBody CreateSavingRuleResource resource, @RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty()) return ResponseEntity.notFound().build();

        UserSetting settings = settingsOpt.get();
        Optional<SavingRule> ruleOpt = settings.getSavingRules().stream()
                .filter(r -> r.getId().equals(ruleId))
                .findFirst();

        if (ruleOpt.isEmpty()) return ResponseEntity.notFound().build();

        SavingRule rule = ruleOpt.get();
        rule.update(resource.name(), resource.isEnabled());
        
        settingsRepository.save(settings);
        return ResponseEntity.ok(SettingsAssembler.toResource(settings));
    }

    @DeleteMapping("/rules/{ruleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete saving rule", description = "Delete an existing saving rule")
    public ResponseEntity<SettingsResource> deleteSavingRule(@PathVariable Long ruleId, @RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty()) return ResponseEntity.notFound().build();

        UserSetting settings = settingsOpt.get();
        Optional<SavingRule> ruleOpt = settings.getSavingRules().stream()
                .filter(r -> r.getId().equals(ruleId))
                .findFirst();

        if (ruleOpt.isEmpty()) return ResponseEntity.notFound().build();

        settings.removeSavingRule(ruleOpt.get());
        settingsRepository.save(settings);
        return ResponseEntity.ok(SettingsAssembler.toResource(settings));
    }

    @PostMapping("/reset")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Reset settings to default", description = "Reset notification, report, and security settings to default values.")
    public ResponseEntity<SettingsResource> resetSettings(@RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isPresent()) {
            UserSetting settings = settingsOpt.get();
            settings.updateNotificationSettings(true, true, true, LocalTime.of(5, 0), LocalTime.of(22, 0));
            settings.updateReportSettings(true, false, false, true, false);
            settings.updateSecuritySettings(false);
            settingsRepository.save(settings);
            return ResponseEntity.ok(SettingsAssembler.toResource(settings));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password from settings", description = "Verify old password and update to new password.")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordResource resource) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        var userOpt = userRepository.findById(userId.id());
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        var user = userOpt.get();
        if (!hashingService.matches(resource.oldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect old password");
        }

        user.updatePassword(hashingService.encode(resource.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/2fa/enable")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Enable 2FA", description = "Enable two factor authentication and return QR code/secret.")
    public ResponseEntity<?> enableTwoFactor(@RequestHeader("Authorization") String authHeader) {
        UserId userId = getUserIdFromToken(authHeader);
        if (userId == null) return ResponseEntity.badRequest().build();

        Optional<UserSetting> settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty()) return ResponseEntity.notFound().build();

        UserSetting settings = settingsOpt.get();
        settings.updateSecuritySettings(true);
        settingsRepository.save(settings);

        Map<String, String> response = new HashMap<>();
        response.put("qrCode", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=otpauth://totp/SEMS:User?secret=JBSWY3DPEHPK3PXP&issuer=SEMS");
        response.put("secret", "JBSWY3DPEHPK3PXP");
        return ResponseEntity.ok(response);
    }
}
