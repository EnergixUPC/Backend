package com.backendsems.news.interfaces.rest.resources;

import java.time.LocalDateTime;

public record NewsItemResource(Long id, String title, String content, boolean isTip, String summary,
                                String imageUrl, String category, String link, LocalDateTime publishedAt) {
}
