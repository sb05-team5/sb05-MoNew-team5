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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArticleProcessor implements ItemProcessor<InterestKeywordDto, Article> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArticleMapper articleMapper;


    @Override
    public Article process(InterestKeywordDto item) throws Exception {
        final String clientId= "7UJkEH_tIBCmVEAY1HXl";
        final String clientSecret= "";
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
                    .source(Source.NAVER.getSource())
                    .sourceUrl(firstItem.path("link").asText())
                    .title(firstItem.path("title").asText().replaceAll("<.*?>", ""))
                    .publishDate(Instant.now())
                    .summary(firstItem.path("description").asText().replaceAll("<.*?>", ""))
                    .commentCount(0L)
                    .viewCount(0)
                    .viewedByBme(false)
                    .build();
            Article article = articleMapper.toEntity(articleDto);
            article=article.toBuilder()
                    .interest_id(interestId)
                    .build();


            return article;

        } catch (WebClientResponseException.TooManyRequests e) {
            Thread.sleep(2000); // 429 처리
            return null; // 재시도 로직 간단히 null 처리
        }

    }
}
