package com.backendsems.helpcenter.infrastructure.repositories.jpa;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
