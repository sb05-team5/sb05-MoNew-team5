package com.sprint.project.monew.article.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.NaverNewsResponseDto;
import com.sprint.project.monew.article.dto.NewsItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NaverNewsService {
    private final String CLIENT_ID = "7UJkEH_tIBCmVEAY1HXl";
    private final String CLIENT_SECRET = "s16qgH3GOb";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public NaverNewsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();
    }

    public List<ArticleDto> searchNews(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", encodedQuery)
                        .queryParam("display", 5)
                        .queryParam("start", 1)
                        .queryParam("sort", "date")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        NaverNewsResponseDto newsResponse = objectMapper.readValue(response, NaverNewsResponseDto.class);

        return newsResponse.getItems().stream()
                .map(item -> ArticleDto.builder()
                        .sourceUrl(item.getLink())
                        .source(item.getOriginallink() != null ? item.getOriginallink() : "Naver")
                        .title(item.getTitle())
                        .publishDate(item.getPublishDate())
                        .summary(item.getDescription())
                        .commentCount(0)
                        .viewCount(0)
                        .viewedByBme(false)
                        .build())
                .collect(Collectors.toList());
    }
}
