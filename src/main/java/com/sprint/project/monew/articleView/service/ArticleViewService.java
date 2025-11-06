package com.sprint.project.monew.articleView.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.articleView.repository.ArticleViewRepository;
import com.sprint.project.monew.log.document.ArticleViewActivity;
import com.sprint.project.monew.log.repository.ArticleViewActivityRepository;
import com.sprint.project.monew.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleViewService {
    private final ArticleViewRepository articleViewRepository;
    private final ArticleViewActivityRepository  articleViewActivityRepository;


    public ArticleView createArticleView(Article article, User user) {

        if( articleViewRepository.existsById( article.getId(),user.getId() ) ){
            throw new IllegalStateException(
                    "User " + user.getId() + " has already viewed article " + article.getId()
            );
        }

        ArticleView articleView = ArticleView.builder()
                .article(article)
                .user(user)
                .createdAt(Instant.now())
                .build();



        return articleViewRepository.save(articleView);
    }


}
