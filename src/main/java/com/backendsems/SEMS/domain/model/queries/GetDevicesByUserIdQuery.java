package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetDevicesByUserIdQuery
 * Query para obtener todos los dispositivos de un usuario.
 */
public record GetDevicesByUserIdQuery(UserId userId) {
}