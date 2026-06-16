package com.backendsems.news.domain.model.commands;

public record CreateNewsItemCommand(String title, String content, boolean isTip) {
}
