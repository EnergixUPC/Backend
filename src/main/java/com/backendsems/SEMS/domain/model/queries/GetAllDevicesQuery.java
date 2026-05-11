package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetAllDevicesQuery
 * Query para obtener todos los dispositivos.
 */
public record GetAllDevicesQuery(UserId userId) {}
