package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Alert;
import com.backendsems.SEMS.domain.model.entities.Notification;
import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.infrastructure.repositories.AlertRepository;
import com.backendsems.SEMS.infrastructure.repositories.NotificationRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    
    // Notification methods
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, "UNREAD");
    }
    
    public Notification createNotification(Long userId, Notification notification) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        notification.setUserId(userId);
        return notificationRepository.save(notification);
    }
    
    public Notification markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead("READ");
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    // Alert methods
    public List<Alert> getAllAlertsByUserId(Long userId) {
        return alertRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public List<Alert> getUnreadAlertsByUserId(Long userId) {
        return alertRepository.findByUserIdAndIsRead(userId, false);
    }
    
    public Alert createAlert(Long userId, Alert alert) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        alert.setUser(user);
        alert.setTimestamp(LocalDateTime.now());
        return alertRepository.save(alert);
    }
    
    public Alert markAlertAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        alert.setIsRead(true);
        return alertRepository.save(alert);
    }
    
    public void deleteAlert(Long alertId) {
        alertRepository.deleteById(alertId);
    }
    
    // Utility methods
    public Long getUnreadNotificationCount(Long userId) {
        return Long.valueOf(notificationRepository.countUnreadByUserId(userId));
    }
    
    public Long getUnreadAlertCount(Long userId) {
        return alertRepository.countByUserIdAndIsRead(userId, false);
    }
}