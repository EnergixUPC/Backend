package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.interfaces.rest.resources.CreateNotificationResource;

/**
 * NotificationFromCommandAssembler
 * Assembler para convertir CreateNotificationCommand a CreateNotificationResource.
 */
public class NotificationFromCommandAssembler {

    public static CreateNotificationResource toResourceFromCommand(CreateNotificationCommand command) {
        return new CreateNotificationResource(
                command.deviceId(),
                command.userId(),
                command.message(),
                command.type()
        );
    }
}