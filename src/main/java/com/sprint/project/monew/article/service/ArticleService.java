package com.sprint.project.monew.article.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String clientId= "7UJkEH_tIBCmVEAY1HXl";

    private final String clientSecret= "s16qgH3GOb";

    public void fetchAndSaveEntertainmentNews() {
        String query = "연예";

        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("query", query)
                            .queryParam("display", 10)
                            .queryParam("sort", "date")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                ArticleDto articleDto = ArticleDto.builder()
                        .id(null) // 새로 생성
                        .source("Naver News")
                        .sourceUrl(item.path("link").asText())
                        .title(item.path("title").asText().replaceAll("<.*?>", ""))

                        .publishDate(LocalDate.now().toString())
                        .summary(item.path("description").asText().replaceAll("<.*?>", ""))
                        .commentCount(0)
                        .viewCount(0)
                        .viewedByBme(false)
                        .build();

                Article article = Article.builder()// 새로 생성
                        .id(null)
                        .source(articleDto.source())
                        .sourceUrl(articleDto.sourceUrl())
                        .title(articleDto.title())
                        .publishDate(LocalDate.now()) // DTO String -> LocalDate 변환 가능
                        .summary(articleDto.summary())
                        .viewCount(articleDto.viewCount())
                        .interest_id(null) // 관심사 없으면 null
                        .build();
                articleRepository.save(article);
            }

            System.out.println("✅ 연예 뉴스 저장 완료 (" + items.size() + "건)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}