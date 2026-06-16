package com.backendsems.news.interfaces.rest.transform;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.interfaces.rest.resources.NewsItemResource;

public class NewsItemResourceFromEntityAssembler {
    public static NewsItemResource toResourceFromEntity(NewsItem entity) {
        return new NewsItemResource(entity.getId(), entity.getTitle(), entity.getContent(), entity.isTip());
    }
}
