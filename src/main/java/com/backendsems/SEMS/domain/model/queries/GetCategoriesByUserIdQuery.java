package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetCategoriesByUserIdQuery
 * Query para obtener categorias por usuario.
 */
public record GetCategoriesByUserIdQuery(UserId userId) {}
