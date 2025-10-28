package com.sprint.project.monew.article.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleRestoreResultDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.entity.Source;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleBackup.repository.ArticleBackupRepository;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.articleView.repository.ArticleViewRepository;
import com.sprint.project.monew.articleView.service.ArticleViewService;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.repository.InterestRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import com.sprint.project.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleViewService articleViewService;
    private final ArticleBackupRepository articleBackupRepository;
    private final ArticleMapper articleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //네이버 API를 위한 키값
    private final String clientId= "7UJkEH_tIBCmVEAY1HXl";
    private final String clientSecret= "";



    public ArticleDto searchOne(UUID articleId,UUID userId) {
        Article article = articleRepository.findByArticleId(articleId).orElse(null);

        User user =userRepository.findById(userId).orElse(null);

        articleViewService.createArticleView(article, user);

        return articleRepository.searchOne(articleId,userId);

    }





    public ArticleRestoreResultDto restore(String from, String to){
        List<ArticleBackup> backups= articleBackupRepository.searchForRestore(from,to);

        List<Article> article = articleRepository.searchForRestore(from,to);
        List<UUID> articleIds = new ArrayList<>();

        for(Article a : article){
            articleIds.add(a.getId());
        }

        List<Article> targets= new ArrayList<>();
        List<String> targetIds = new ArrayList<>();
        int count = 0;
        for ( ArticleBackup backup : backups){
            if( !articleIds.contains(backup.getArticle_id()) ){
                targetIds.add(backup.getArticle_id().toString());
                count++;
                Article target= Article.builder()
                        .id(backup.getArticle_id())
                        .createdAt(backup.getCreatedAt())
                        .publishDate(backup.getPublishDate())
                        .source(backup.getSource())
                        .sourceUrl(backup.getSourceUrl())
                        .title(backup.getTitle())
                        .summary(backup.getSummary())
                        .viewCount(backup.getViewCount())
                        .deleted_at(backup.getDeleted_at())
                        .interest_id(backup.getInterest_id())
                        .build();
                targets.add(target);
            }
        }

        articleRepository.saveAll(targets);





        return ArticleRestoreResultDto.builder()
                .restoreDate(Instant.now())
                .restoredArticleIds(targetIds)
                .restoredArticleCount(count)
                .build();
    }


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



    public List<String> searchSource(){
        List<String> sources = articleRepository.searchSource();

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

    // articles, article_views , comments, comments_likes 전부 논리삭제 처리 해야됨.
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
    public void naverFetchAndSaveNews() throws InterruptedException {

        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        List<Interest> interests = interestRepository.findAll();
        if( interests.isEmpty()) {
            return;
        }else{
            for(Interest interest : interests) {
                //List<String> keywords = Arrpays.asList(item.path("keywords").asText().split(","));
                List<String> keywords = interest.getKeywords();
                if(keywords == null || keywords.isEmpty()) {
                    continue;
                }
                for (String keyword : keywords) {
                    try {
                        String response = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .queryParam("query", keyword)
                                        .queryParam("display", 1)
                                        .queryParam("sort", "date")
                                        .build())
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                        JsonNode root = objectMapper.readTree(response);
                        JsonNode items = root.path("items");
                        int count = 0;

                        for (JsonNode item : items) {
                            UUID interestId = interest.getId();

                            ArticleDto articleDto = ArticleDto.builder()
                                    .id(null) // 새로 생성
                                    .source(Source.NAVER.getSource())
                                    .sourceUrl(item.path("link").asText())
                                    .title(item.path("title").asText().replaceAll("<.*?>", ""))
                                    .publishDate(Instant.now())
                                    .summary(item.path("description").asText().replaceAll("<.*?>", ""))
                                    .commentCount(0L)
                                    .viewCount(0)
                                    .viewedByBme(false)
                                    .build();
                            Article article = articleMapper.toEntity(articleDto);
                            article=article.toBuilder()
                                    .interest_id(interestId)
                                    .build();
                            try {
                                articleRepository.save(article);
                                count += 1;
                            } catch (DataIntegrityViolationException e) {
                                // 중복 발생 등 DB 제약 조건 위반 시 로깅 후 건너뜀
                                log.error("API->save부분에서 중복 또는 오류 발생",e);
                                continue;
                            }
                            Thread.sleep(700);

                        }

                        log.info("API완료!!! -- ✅ "+ keyword +"뉴스 저장 완료 (" + count + "건)");
                    }catch (WebClientResponseException.TooManyRequests e) {
                        log.warn("429 Too Many Requests 발생, 2초 대기 후 재시도");
                        Thread.sleep(2000);
                        // 재시도 로직 (간단히 한 번 더 시도)
                        continue;
                    }catch (Exception e) {
                        log.error("API부분에서 예외 발생", e);
                    }
                }
            }
        }
    }
}