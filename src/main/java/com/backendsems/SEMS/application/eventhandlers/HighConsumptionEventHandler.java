package com.backendsems.SEMS.application.eventhandlers;

import com.backendsems.SEMS.domain.model.entities.Notification;
import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.infrastructure.repositories.NotificationRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * HighConsumptionEventHandler
 * Maneja eventos de alto consumo creando notificaciones
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HighConsumptionEventHandler {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @EventListener
    @Transactional
    public void handle(HighConsumptionDetectedEvent event) {
        log.info("Procesando evento de alto consumo para dispositivo: {}", event.getDeviceId());
        
        // Buscar el usuario
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Crear notificación
        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .title("Alto Consumo Detectado")
                .message(String.format("El dispositivo %s ha excedido el consumo normal: %.2f kWh", 
                        event.getDeviceName(), event.getConsumptionValue()))
                .type(Notification.NotificationType.WARNING)
                .isRead("UNREAD")
                .timestamp(LocalDateTime.now())
                .build();
        
        // Guardar notificación
        notificationRepository.save(notification);
        
        log.info("Notificación de alto consumo creada para usuario: {}", user.getId());
    }
}