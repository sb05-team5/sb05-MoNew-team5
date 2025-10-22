package com.sprint.project.monew.article.controller;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.service.NaverNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/news")
public class NaverNewsController {

    private final NaverNewsService naverNewsService;


    @GetMapping("/")
    public List<ArticleDto> getNews(@RequestParam String query) throws Exception {
        return naverNewsService.searchNews(query);
    }
}
