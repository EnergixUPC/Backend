package com.backendsems.news.infrastructure.repositories.jpa;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {
}
