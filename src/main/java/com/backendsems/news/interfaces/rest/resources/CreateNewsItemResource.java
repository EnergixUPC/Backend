package com.backendsems.news.interfaces.rest.resources;

public record CreateNewsItemResource(String title, String content, boolean isTip, String summary,
                                      String imageUrl, String category, String link) {

    public CreateNewsItemResource(String title, String content, boolean isTip) {
        this(title, content, isTip, null, null, null, null);
    }
}
