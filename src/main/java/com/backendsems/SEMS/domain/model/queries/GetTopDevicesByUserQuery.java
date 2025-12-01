package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetTopDevicesByUserQuery
 * Query para obtener los dispositivos con mayor consumo de un usuario.
 */
public record GetTopDevicesByUserQuery(UserId userId, int limit, String period) {
    public GetTopDevicesByUserQuery(UserId userId, int limit) {
        this(userId, limit, "daily");
    }
}