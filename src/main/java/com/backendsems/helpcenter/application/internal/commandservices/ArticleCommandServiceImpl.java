package com.backendsems.helpcenter.application.internal.commandservices;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.domain.model.commands.CreateArticleCommand;
import com.backendsems.helpcenter.domain.services.ArticleCommandService;
import com.backendsems.helpcenter.infrastructure.repositories.jpa.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleCommandServiceImpl implements ArticleCommandService {

    private final ArticleRepository articleRepository;

    public ArticleCommandServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Optional<Article> handle(CreateArticleCommand command) {
        Article article = new Article(command.title(), command.content());
        articleRepository.save(article);
        return Optional.of(article);
    }
}
