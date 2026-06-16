package com.backendsems.controllertest;

import com.backendsems.helpcenter.domain.model.aggregates.Article;
import com.backendsems.helpcenter.domain.model.commands.CreateArticleCommand;
import com.backendsems.helpcenter.domain.model.queries.GetAllArticlesQuery;
import com.backendsems.helpcenter.domain.services.ArticleCommandService;
import com.backendsems.helpcenter.domain.services.ArticleQueryService;
import com.backendsems.helpcenter.interfaces.rest.HelpCenterController;
import com.backendsems.helpcenter.interfaces.rest.resources.ArticleResource;
import com.backendsems.helpcenter.interfaces.rest.resources.CreateArticleResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HelpCenterControllerTest {

    @Mock
    private ArticleCommandService articleCommandService;

    @Mock
    private ArticleQueryService articleQueryService;

    @InjectMocks
    private HelpCenterController helpCenterController;

    @Test
    void createArticle_Success() {
        CreateArticleResource resource = new CreateArticleResource("Title", "Content");
        Article article = new Article("Title", "Content");
        Mockito.when(articleCommandService.handle(any(CreateArticleCommand.class))).thenReturn(Optional.of(article));

        ResponseEntity<ArticleResource> response = helpCenterController.createArticle(resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Title", response.getBody().title());
    }

    @Test
    void getAllArticles_Success() {
        Article article = new Article("Title", "Content");
        Mockito.when(articleQueryService.handle(any(GetAllArticlesQuery.class))).thenReturn(Collections.singletonList(article));

        ResponseEntity<List<ArticleResource>> response = helpCenterController.getAllArticles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Title", response.getBody().get(0).title());
    }
}
