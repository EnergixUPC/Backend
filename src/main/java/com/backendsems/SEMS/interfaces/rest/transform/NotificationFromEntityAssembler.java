package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.interfaces.rest.resources.NotificationResource;

/**
 * NotificationFromEntityAssembler
 * Assembler para convertir Notification a NotificationResource.
 */
public class NotificationFromEntityAssembler {

    public static NotificationResource toResourceFromEntity(Notification entity) {
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