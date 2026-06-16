package com.backendsems.helpcenter.domain.services;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.domain.model.queries.GetAllArticlesQuery;
import java.util.List;

public interface ArticleQueryService {
    List<Article> handle(GetAllArticlesQuery query);
}
