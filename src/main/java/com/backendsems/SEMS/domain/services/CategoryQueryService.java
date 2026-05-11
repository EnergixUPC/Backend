package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Category;
import com.backendsems.SEMS.domain.model.queries.GetCategoriesByUserIdQuery;
import java.util.List;

/**
 * CategoryQueryService
 * Servicio de queries para categorias.
 */
public interface CategoryQueryService {

    /**
     * Maneja la query para obtener categorias por userId.
     * @param query La query GetCategoriesByUserIdQuery.
     * @return Lista de categorias.
     */
    List<Category> handle(GetCategoriesByUserIdQuery query);
}
