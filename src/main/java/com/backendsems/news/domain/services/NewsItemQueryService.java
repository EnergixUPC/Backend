package com.backendsems.news.domain.services;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.queries.GetAllNewsItemsQuery;
import java.util.List;

public interface NewsItemQueryService {
    List<NewsItem> handle(GetAllNewsItemsQuery query);
}
