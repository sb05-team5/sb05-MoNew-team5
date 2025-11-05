package com.sprint.project.monew.articleBackup.backupBatch.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.entity.Source;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.articleBackup.backupBatch.dto.InterestKeywordDto;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArticleProcessor implements ItemProcessor<InterestKeywordDto, Article> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArticleMapper articleMapper;
    @Value("${CLIENT_SECRET}")
    private String clientSecret;
    private Instant parseToInstant(String text, ZoneId zone, DateTimeFormatter formatter) {
        if (text == null || text.isBlank()) return null;
        try {
            if (text.endsWith("Z")) {
                return Instant.parse(text);
            } else {
                LocalDateTime ldt = LocalDateTime.parse(text, formatter);
                return ldt.atZone(zone).toInstant();
            }
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    @Override
    public Article process(InterestKeywordDto item) throws Exception {
        final String clientId= "sA54zNU2aIsWrCZ0RL8K";

        // 공통 Zone과 Formatter
        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");


        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("query", item.getKeyword())
                            .queryParam("display", 1)
                            .queryParam("sort", "date")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode firstItem = root.path("items").get(0); // display=1 기준

            if (firstItem == null) return null;

            // Builder로 interestId 포함해서 바로 생성
            UUID interestId = item.getInterestId();

            ArticleDto articleDto = ArticleDto.builder()
                    .id(null) // 새로 생성
                    .createdAt(parseToInstant(Instant.now().toString(), zone, dateTimeFormatter))
                    .source(Source.NAVER.getSource())
                    .sourceUrl(firstItem.path("link").asText().replaceAll("<.*?>", ""))
                    .title(firstItem.path("title").asText().replaceAll("<.*?>", ""))
                    .publishDate(firstItem.path("pubDate").asText().replaceAll("<.*?>",""))
                    .summary(firstItem.path("description").asText().replaceAll("<.*?>", ""))
                    .viewCount(0)
                    .viewedByBme(false)
                    .build();
            Article article = articleMapper.toEntity(articleDto);
            article=article.toBuilder()
                    .interest_id(interestId)
                    .build();

            Thread.sleep(700);
            return article;

        } catch (WebClientResponseException.TooManyRequests e) {
            Thread.sleep(2000); // 429 처리
            return null; // 재시도 로직 간단히 null 처리
        }

    }
}
