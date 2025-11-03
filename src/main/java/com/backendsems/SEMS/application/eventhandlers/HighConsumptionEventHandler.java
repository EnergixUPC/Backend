package com.backendsems.SEMS.application.eventhandlers;

import com.backendsems.SEMS.domain.model.aggregates.NotificationAggregate;
import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.infrastructure.repositories.NotificationRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        UserAggregate user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Crear notificación
        NotificationAggregate notification = NotificationAggregate.createHighConsumptionAlert(
                user,
                event.getDeviceName(),
                event.getConsumptionValue()
        );
        
        // Guardar notificación
        notificationRepository.save(notification);
        
        log.info("Notificación de alto consumo creada para usuario: {}", user.getId());
    }
}