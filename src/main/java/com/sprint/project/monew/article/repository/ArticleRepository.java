package com.sprint.project.monew.article.repository;


import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleView.entity.ArticleView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


public interface ArticleRepository extends JpaRepository<Article, UUID>,ArticleQueryRepository  {

}
