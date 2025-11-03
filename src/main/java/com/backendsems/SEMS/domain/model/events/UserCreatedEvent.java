package com.backendsems.SEMS.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserCreatedEvent
 * Evento de dominio que se dispara cuando se crea un nuevo usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    
    private Long userId;
    private String email;
    private String fullName;
    private LocalDateTime occurredOn;
    
    public static UserCreatedEvent create(Long userId, String email, String fullName) {
        return UserCreatedEvent.builder()
                .userId(userId)
                .email(email)
                .fullName(fullName)
                .occurredOn(LocalDateTime.now())
                .build();
    }
}