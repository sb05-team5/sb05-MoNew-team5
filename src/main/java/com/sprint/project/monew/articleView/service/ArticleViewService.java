package com.sprint.project.monew.articleView.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.articleView.repository.ArticleViewRepository;
import com.sprint.project.monew.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleViewService {
    private final ArticleViewRepository articleViewRepository;

    public ArticleView createArticleView(Article article, User user) {
        ArticleView articleView = ArticleView.builder()
                .id(article.getId())
                .user(user)
                .build();


        return articleViewRepository.save(articleView);
    }


}
