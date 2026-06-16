package com.backendsems.news.interfaces.rest;

import com.backendsems.news.domain.model.queries.GetAllNewsItemsQuery;
import com.backendsems.news.domain.services.NewsItemCommandService;
import com.backendsems.news.domain.services.NewsItemQueryService;
import com.backendsems.news.interfaces.rest.resources.NewsItemResource;
import com.backendsems.news.interfaces.rest.resources.CreateNewsItemResource;
import com.backendsems.news.interfaces.rest.transform.NewsItemResourceFromEntityAssembler;
import com.backendsems.news.interfaces.rest.transform.CreateNewsItemCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/news", produces = APPLICATION_JSON_VALUE)
@Tag(name = "News and Tips", description = "News and Tips Management Endpoints")
public class NewsController {

    private final NewsItemCommandService newsItemCommandService;
    private final NewsItemQueryService newsItemQueryService;

    public NewsController(NewsItemCommandService newsItemCommandService, NewsItemQueryService newsItemQueryService) {
        this.newsItemCommandService = newsItemCommandService;
        this.newsItemQueryService = newsItemQueryService;
    }

    @PostMapping
    public ResponseEntity<NewsItemResource> createNewsItem(@RequestBody CreateNewsItemResource resource) {
        var command = CreateNewsItemCommandFromResourceAssembler.toCommandFromResource(resource);
        var newsItem = newsItemCommandService.handle(command);
        if (newsItem.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var newsItemResource = NewsItemResourceFromEntityAssembler.toResourceFromEntity(newsItem.get());
        return new ResponseEntity<>(newsItemResource, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NewsItemResource>> getAllNewsItems() {
        var newsItems = newsItemQueryService.handle(new GetAllNewsItemsQuery());
        var newsItemResources = newsItems.stream()
                .map(NewsItemResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(newsItemResources);
    }
}
