package com.backendsems.news.domain.model.aggregates;

import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
@Entity
public class NewsItem extends AuditableAbstractAggregateRoot<NewsItem> {
    
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private boolean isTip;

    public NewsItem() {}

    public NewsItem(String title, String content, boolean isTip) {
        this.title = title;
        this.content = content;
        this.isTip = isTip;
    }
}
