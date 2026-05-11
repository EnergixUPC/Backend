package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.commands.CreateCategoryCommand;
import com.backendsems.SEMS.domain.model.entities.Category;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.CategoryCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

/**
 * CategoryCommandServiceImpl
 * Implementacion del servicio de comandos para categorias.
 */
@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;

    public CategoryCommandServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Long handle(CreateCategoryCommand command, UserId userId) {
        Category category = new Category(userId, command.name());
        return categoryRepository.save(category).getId();
    }
}
