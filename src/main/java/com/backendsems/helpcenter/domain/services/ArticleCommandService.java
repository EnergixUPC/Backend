package com.backendsems.helpcenter.domain.services;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.domain.model.commands.CreateArticleCommand;
import java.util.Optional;

public interface ArticleCommandService {
    Optional<Article> handle(CreateArticleCommand command);
}
