package com.sprint.project.monew.articleView.repository;

import com.sprint.project.monew.articleView.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID>,ArticleViewQueryRepository {
}
