package com.backendsems.SEMS.domain.model.queries;

/**
 * DeviceExistsQuery
 * Query para verificar si un dispositivo existe por su ID.
 */
public record DeviceExistsQuery(Long deviceId) {
}