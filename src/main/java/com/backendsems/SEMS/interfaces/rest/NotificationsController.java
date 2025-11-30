package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.domain.services.NotificationQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.CreateNotificationResource;
import com.backendsems.SEMS.interfaces.rest.resources.NotificationResource;
import com.backendsems.SEMS.interfaces.rest.transform.NotificationFromCommandAssembler;
import com.backendsems.SEMS.interfaces.rest.transform.NotificationFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NotificationsController
 * Controlador REST para notificaciones.
 */
@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notifications Management Endpoints")
public class NotificationsController {

    private final NotificationCommandService commandService;
    private final NotificationQueryService queryService;

    public NotificationsController(NotificationCommandService commandService, NotificationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a new notification")
    public ResponseEntity<CreateNotificationResource> createNotification(@RequestBody CreateNotificationResource resource) {
        CreateNotificationCommand command = new CreateNotificationCommand(
                resource.deviceId(),
                resource.userId(),
                resource.message(),
                resource.type()
        );
        commandService.createNotification(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(NotificationFromCommandAssembler.toResourceFromCommand(command));
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "Get notifications by device ID")
    public ResponseEntity<List<NotificationResource>> getNotificationsByDeviceId(@PathVariable Long deviceId) {
        // Note: Assuming DeviceId is Long, adjust if needed
        List<NotificationResource> resources = queryService.getNotificationsByDeviceId(new com.backendsems.SEMS.domain.model.valueobjects.DeviceId(deviceId))
                .stream()
                .map(NotificationFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID")
    public ResponseEntity<List<NotificationResource>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResource> resources = queryService.getNotificationsByUserId(new com.backendsems.SEMS.domain.model.valueobjects.UserId(userId))
                .stream()
                .map(NotificationFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<NotificationResource>> getAllNotifications() {
        List<NotificationResource> resources = queryService.getAllNotifications()
                .stream()
                .map(NotificationFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}