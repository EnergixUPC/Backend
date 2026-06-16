package com.backendsems.helpcenter.domain.model.aggregates;

import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
@Entity
public class Article extends AuditableAbstractAggregateRoot<Article> {
    
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    public Article() {}

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
