package com.sprint.project.monew.article.controller;

import com.sprint.project.monew.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    // ✅ 연예 뉴스 가져와서 DB 저장
    @GetMapping("/articles/entertainment")
    public String fetchEntertainmentNews() {
        articleService.fetchAndSaveEntertainmentNews();
        return "연예 뉴스 수집 완료";
    }
}
