package com.backendsems.helpcenter.application.internal.queryservices;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.domain.model.queries.GetAllArticlesQuery;
import com.backendsems.helpcenter.domain.services.ArticleQueryService;
import com.backendsems.helpcenter.infrastructure.repositories.jpa.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleQueryServiceImpl implements ArticleQueryService {

    private final ArticleRepository articleRepository;

    public ArticleQueryServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> handle(GetAllArticlesQuery query) {
        return articleRepository.findAll();
    }
}
