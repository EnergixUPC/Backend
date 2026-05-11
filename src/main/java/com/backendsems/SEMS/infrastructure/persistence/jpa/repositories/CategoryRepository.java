package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.Category;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CategoryRepository
 * Repositorio JPA para Category.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Encuentra categorias por userId.
     * @param userId El ID del usuario.
     * @return Lista de categorias.
     */
    List<Category> findByUserId(UserId userId);
}
