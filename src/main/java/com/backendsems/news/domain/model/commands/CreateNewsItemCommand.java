package com.backendsems.news.domain.model.commands;

public record CreateNewsItemCommand(String title, String content, boolean isTip, String summary,
                                     String imageUrl, String category, String link) {
}
