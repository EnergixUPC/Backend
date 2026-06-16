package com.backendsems.news.interfaces.rest.resources;

public record NewsItemResource(Long id, String title, String content, boolean isTip) {
}
