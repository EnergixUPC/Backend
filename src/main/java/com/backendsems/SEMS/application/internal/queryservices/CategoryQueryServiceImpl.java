package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.entities.Category;
import com.backendsems.SEMS.domain.model.queries.GetCategoriesByUserIdQuery;
import com.backendsems.SEMS.domain.services.CategoryQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * CategoryQueryServiceImpl
 * Implementacion del servicio de queries para categorias.
 */
@Service
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> handle(GetCategoriesByUserIdQuery query) {
        return categoryRepository.findByUserId(query.userId());
    }
}
