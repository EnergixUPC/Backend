package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.aggregates.NotificationAggregate;
import com.backendsems.SEMS.domain.model.entities.Notification;
import com.backendsems.SEMS.domain.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<NotificationAggregate> aggregates = notificationService.getAllNotificationsByUserId(userId);
            List<Notification> notifications = aggregates.stream()
                    .map(aggregate -> Notification.fromAggregate(aggregate, null)) // Usar null temporalmente
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            List<NotificationAggregate> aggregates = notificationService.getUnreadNotificationsByUserId(userId);
            List<Notification> notifications = aggregates.stream()
                    .map(aggregate -> Notification.fromAggregate(aggregate, null)) // Usar null temporalmente
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification,
            Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            NotificationAggregate notificationAggregate = notification.toAggregate();
            NotificationAggregate createdAggregate = notificationService.createNotification(userId, notificationAggregate);
            Notification createdNotification = Notification.fromAggregate(createdAggregate, null);
            return ResponseEntity.ok(createdNotification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId) {
        try {
            NotificationAggregate aggregate = notificationService.markNotificationAsRead(notificationId);
            Notification notification = Notification.fromAggregate(aggregate, null);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/count/unread")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        try {
            Long userId = 1L; // TODO: Extraer del JWT
            Long count = notificationService.getUnreadNotificationCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}