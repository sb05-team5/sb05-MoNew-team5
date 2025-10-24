package com.sprint.project.monew.article.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.entity.Source;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.common.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //네이버 API를 위한 키값
    private final String clientId= "7UJkEH_tIBCmVEAY1HXl";
    private final String clientSecret= "eEst2pXmu1";
































    public CursorPageResponse<ArticleDto> searchPageSortedArticle(
                                                        String keyword, String interestId,
                                                        String sourceIn,
                                                        String publishDateFrom, String publishDateTo,
                                                        String orderBy, String direction,
                                                        String cursor, String after, int limit,
                                                        UUID userId
    ){
        log.info("servie --> keyword={}, interestId={}, sourceIn={}, publishDateFrom={}, publishDateTo={}, orderBy={}, direction={}, cursor={}, after={}, limit={}, userId={}",
                keyword, interestId, sourceIn,
                publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);

        return articleRepository.searchPageSortedArticle(keyword,interestId,sourceIn,
                publishDateFrom,publishDateTo,orderBy,direction,cursor,after,limit,userId);
    }



    public List<String> getSource(){
        List<Article> articles = articleRepository.findAll();
        List<String> sources = new ArrayList<>();
        if(articles.isEmpty()){
            return sources;
        }
        for (Article article : articles) {
            if( !sources.contains(article.getSource()) ) {
                sources.add(article.getSource().toString());
            }
        }
        return sources;
    }


    public void hardDelete(UUID articleId){
        articleRepository.findById(articleId).ifPresent(article -> {
            //articleView 삭제 - articleId를 가지고 있는 view 삭제
            //comments_likes 삭제 - articleId를 가지고 있는 comments 찾고 그 comment의 ID를 가지고 있는
            //                     like를 찾아서 삭제
            //comments 삭제 - 앞에서 찾아놨던 comments들 지우기

            //전체 구현이 되어 있지 않으면 오류가 발생하기 때문에 일단은 주석처리
            // articleRepository.deleteById(articleId);
        });

    }


    public void softDelete(UUID articleId){
        articleRepository.findById(articleId).ifPresent(article -> {
            if(article.getDeleted_at() == null) {
                Article updated = article.toBuilder().deleted_at(Instant.now()).build();
                articleRepository.save(updated);
            }
        });
    }




    //네이버 뉴스 API에서 데이터 받아오는 함수
    //중복Url을 피해서 저장하는 것까지 확인 - 다른 상황에서도 동작하는지 추후에 확인
    public void naverFetchAndSaveNews() {
        String query = "축구";

        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("query", query)
                            .queryParam("display", 5)
                            .queryParam("sort", "date")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            int count = 0;
            for (JsonNode item : items) {
                ArticleDto articleDto = ArticleDto.builder()
                        .id(null) // 새로 생성
                        .source(Source.NAVER.getSource())
                        .sourceUrl(item.path("link").asText())
                        .title(item.path("title").asText().replaceAll("<.*?>", ""))
                        .publishDate(Instant.now().toString())
                        .summary(item.path("description").asText().replaceAll("<.*?>", ""))
                        .commentCount(0)
                        .viewCount(0)
                        .viewedByBme(false)
                        .build();
                Article article = articleMapper.toEntity(articleDto);
                article=article.toBuilder()
                        .interest_id(UUID.fromString("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1"))
                        .build();

                // sourceUrl 중복시 예외 발생으로 죽어버리는 것을 방지하기 위한 try문
                try {
                    articleRepository.save(article);
                    count += 1;
                } catch (DataIntegrityViolationException e) {
                    // 중복 발생 등 DB 제약 조건 위반 시 로깅 후 건너뜀
                    log.error("API->save부분에서 중복 또는 오류 발생",e);
                }
            }

            log.info("API완료!!! -- ✅ 축구 뉴스 저장 완료 (" + count + "건)");
        } catch (Exception e) {
            log.error("API부분에서 예외 발생",e);
        }
    }
}