package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetNotificationsByUserIdQuery
 * Query para obtener notificaciones por userId.
 */
public record GetNotificationsByUserIdQuery(UserId userId) {
}