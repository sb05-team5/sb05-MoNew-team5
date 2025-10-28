package com.sprint.project.monew.article.controller;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleRestoreResultDto;
import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.common.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    // ✅ naver 뉴스 가져와서 DB 저장 - 관심사(키워드)가 추가되면 그 키워드가 포함되는 기사만 수집할 수 있게 수정 필요
    // batch로 작성해서 안 쓰지만 일단 놔둠
    @GetMapping("/news")
    public void fetchEntertainmentNews() throws InterruptedException {
        articleService.naverFetchAndSaveNews();
    }


    @GetMapping("/{articleId}")
    public ArticleDto searchOne(@PathVariable("articleId") UUID articleId,
                                @RequestHeader("Monew-Request-User-ID") UUID userId
                                ) throws InterruptedException {

        return articleService.searchOne(articleId,userId);
    }



    @GetMapping("/restore")
    public List<ArticleRestoreResultDto> restore(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) throws InterruptedException {

        return Collections.singletonList(articleService.restore(from, to));
    }


    @GetMapping("")
    public CursorPageResponse<ArticleDto> searchArticles(@RequestParam(required = false)String keyword,
                                                         @RequestParam(required = false) String interestId,
                                                         @RequestParam(required = false) String sourceIn,   //콤마스플릿이 맞음.
                                                         @RequestParam(required = false) String publishDateFrom,
                                                         @RequestParam(required = false) String publishDateTo,
                                                         @RequestParam(required = true,defaultValue = "publishDate") String orderBy,
                                                         @RequestParam(required = true,defaultValue = "ASC") String direction,
                                                         @RequestParam(required = true) String cursor,
                                                         @RequestParam(required = true) String after,
                                                         @RequestParam(required = true,defaultValue = "50") int limit,
                                                         @RequestHeader("Monew-Request-User-ID") UUID userId
                                                         ){
      log.info("keyword={}, interestId={}, sourceIn={}, publishDateFrom={}, publishDateTo={}, orderBy={}, direction={}, cursor={}, after={}, limit={}, userId={}",
              keyword, interestId, sourceIn,
              publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);



       return articleService.searchPageSortedArticle(keyword, interestId, sourceIn,
                publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);

    }




    //추후에 전체적인 틀이 잡히면 작업
    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleView> fetchArticleViews(@PathVariable UUID articleId ,
                                                         @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {


        return ResponseEntity.ok().body(new ArticleView());
    }



    //수집된 기사의 출처에 포함되는 것만 중복되지 않게 가지고 오기 성공
    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {

        return ResponseEntity.ok().body(articleService.searchSource());

    }



    //삭제 부분은 추후에 프론트의 스펙과 맞지 않으면 수정 필요
    //논리 삭제 부분은 article만 논리 삭제하면 다른 부분이 나오지 않는지,
    // 다른 부분까지 모두 논리 삭제 처리해야 보이지 않는지 확인 필요
    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable("articleId") UUID articleId) {
        articleService.hardDelete(articleId);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> softDelete(@PathVariable("articleId") UUID articleId) {
        articleService.softDelete(articleId);
        return ResponseEntity.noContent().build();
    }



}
