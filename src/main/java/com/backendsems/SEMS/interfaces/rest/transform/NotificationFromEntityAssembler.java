package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.NotificationEntity;
import com.backendsems.SEMS.interfaces.rest.resources.NotificationResource;

/**
 * NotificationFromEntityAssembler
 * Assembler para convertir NotificationEntity a NotificationResource.
 */
public class NotificationFromEntityAssembler {

    public static NotificationResource toResourceFromEntity(NotificationEntity entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getDeviceId(),
                entity.getUserId(),
                entity.getMessage(),
                entity.getType(),
                entity.getTimestamp()
        );
    }
}