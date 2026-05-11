package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.Category;
import com.backendsems.SEMS.interfaces.rest.resources.CategoryResource;

/**
 * CategoryFromEntityAssembler
 * Ensamblador para convertir Category a CategoryResource.
 */
public class CategoryFromEntityAssembler {

    /**
     * Convierte una Category a CategoryResource.
     * @param category La entidad Category.
     * @return CategoryResource.
     */
    public static CategoryResource toResource(Category category) {
        return new CategoryResource(
                category.getId(),
                category.getName(),
                String.valueOf(category.getUserId().id())
        );
    }
}
