package com.backendsems.news.interfaces.rest.resources;

public record CreateNewsItemResource(String title, String content, boolean isTip) {
}
