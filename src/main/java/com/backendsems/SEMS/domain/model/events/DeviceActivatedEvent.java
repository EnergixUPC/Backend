package com.backendsems.SEMS.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DeviceActivatedEvent
 * Evento de dominio que se dispara cuando se activa un dispositivo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceActivatedEvent {
    
    private Long deviceId;
    private String deviceName;
    private Long userId;
    private LocalDateTime occurredOn;
    
    public static DeviceActivatedEvent create(Long deviceId, String deviceName, Long userId) {
        return DeviceActivatedEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .userId(userId)
                .occurredOn(LocalDateTime.now())
                .build();
    }
}