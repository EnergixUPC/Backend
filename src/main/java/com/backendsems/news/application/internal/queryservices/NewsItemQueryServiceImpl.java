package com.backendsems.news.application.internal.queryservices;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.queries.GetAllNewsItemsQuery;
import com.backendsems.news.domain.services.NewsItemQueryService;
import com.backendsems.news.infrastructure.repositories.jpa.NewsItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsItemQueryServiceImpl implements NewsItemQueryService {

    private final NewsItemRepository newsItemRepository;

    public NewsItemQueryServiceImpl(NewsItemRepository newsItemRepository) {
        this.newsItemRepository = newsItemRepository;
    }

    @Override
    public List<NewsItem> handle(GetAllNewsItemsQuery query) {
        return newsItemRepository.findAll();
    }
}
