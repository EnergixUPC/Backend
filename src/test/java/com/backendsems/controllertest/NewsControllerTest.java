package com.backendsems.controllertest;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.commands.CreateNewsItemCommand;
import com.backendsems.news.domain.model.queries.GetAllNewsItemsQuery;
import com.backendsems.news.domain.services.NewsItemCommandService;
import com.backendsems.news.domain.services.NewsItemQueryService;
import com.backendsems.news.interfaces.rest.NewsController;
import com.backendsems.news.interfaces.rest.resources.CreateNewsItemResource;
import com.backendsems.news.interfaces.rest.resources.NewsItemResource;
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
public class NewsControllerTest {

    @Mock
    private NewsItemCommandService newsItemCommandService;

    @Mock
    private NewsItemQueryService newsItemQueryService;

    @InjectMocks
    private NewsController newsController;

    @Test
    void createNewsItem_Success() {
        CreateNewsItemResource resource = new CreateNewsItemResource("Title", "Content", true);
        NewsItem newsItem = new NewsItem("Title", "Content", true);
        Mockito.when(newsItemCommandService.handle(any(CreateNewsItemCommand.class))).thenReturn(Optional.of(newsItem));

        ResponseEntity<NewsItemResource> response = newsController.createNewsItem(resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Title", response.getBody().title());
        assertEquals(true, response.getBody().isTip());
    }

    @Test
    void getAllNewsItems_Success() {
        NewsItem newsItem = new NewsItem("Title", "Content", true);
        Mockito.when(newsItemQueryService.handle(any(GetAllNewsItemsQuery.class))).thenReturn(Collections.singletonList(newsItem));

        ResponseEntity<List<NewsItemResource>> response = newsController.getAllNewsItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Title", response.getBody().get(0).title());
    }
}
