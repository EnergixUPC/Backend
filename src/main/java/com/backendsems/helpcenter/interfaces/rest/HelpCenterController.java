package com.backendsems.helpcenter.interfaces.rest;

import com.backendsems.helpcenter.domain.model.queries.GetAllArticlesQuery;
import com.backendsems.helpcenter.domain.services.ArticleCommandService;
import com.backendsems.helpcenter.domain.services.ArticleQueryService;
import com.backendsems.helpcenter.interfaces.rest.resources.ArticleResource;
import com.backendsems.helpcenter.interfaces.rest.resources.CreateArticleResource;
import com.backendsems.helpcenter.interfaces.rest.transform.ArticleResourceFromEntityAssembler;
import com.backendsems.helpcenter.interfaces.rest.transform.CreateArticleCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/help-center/articles", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Help Center", description = "Help Center Management Endpoints")
public class HelpCenterController {

    private final ArticleCommandService articleCommandService;
    private final ArticleQueryService articleQueryService;

    public HelpCenterController(ArticleCommandService articleCommandService, ArticleQueryService articleQueryService) {
        this.articleCommandService = articleCommandService;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity<ArticleResource> createArticle(@RequestBody CreateArticleResource resource) {
        var command = CreateArticleCommandFromResourceAssembler.toCommandFromResource(resource);
        var article = articleCommandService.handle(command);
        if (article.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var articleResource = ArticleResourceFromEntityAssembler.toResourceFromEntity(article.get());
        return new ResponseEntity<>(articleResource, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArticleResource>> getAllArticles() {
        var articles = articleQueryService.handle(new GetAllArticlesQuery());
        var articleResources = articles.stream()
                .map(ArticleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(articleResources);
    }
}
