package com.backendsems.helpcenter.interfaces.rest.transform;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.interfaces.rest.resources.ArticleResource;

public class ArticleResourceFromEntityAssembler {
    public static ArticleResource toResourceFromEntity(Article entity) {
        return new ArticleResource(entity.getId(), entity.getTitle(), entity.getContent());
    }
}
