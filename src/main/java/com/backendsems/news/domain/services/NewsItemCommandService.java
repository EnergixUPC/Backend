package com.backendsems.news.domain.services;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.commands.CreateNewsItemCommand;
import java.util.Optional;

public interface NewsItemCommandService {
    Optional<NewsItem> handle(CreateNewsItemCommand command);
}
