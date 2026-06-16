package com.backendsems.news.application.internal.commandservices;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.commands.CreateNewsItemCommand;
import com.backendsems.news.domain.services.NewsItemCommandService;
import com.backendsems.news.infrastructure.repositories.jpa.NewsItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsItemCommandServiceImpl implements NewsItemCommandService {

    private final NewsItemRepository newsItemRepository;

    public NewsItemCommandServiceImpl(NewsItemRepository newsItemRepository) {
        this.newsItemRepository = newsItemRepository;
    }

    @Override
    public Optional<NewsItem> handle(CreateNewsItemCommand command) {
        NewsItem newsItem = new NewsItem(command.title(), command.content(), command.isTip());
        newsItemRepository.save(newsItem);
        return Optional.of(newsItem);
    }
}
