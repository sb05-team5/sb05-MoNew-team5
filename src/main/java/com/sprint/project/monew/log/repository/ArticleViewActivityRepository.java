package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.ArticleViewActivity;
import com.sprint.project.monew.log.document.SubscriptionActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ArticleViewActivityRepository extends MongoRepository<ArticleViewActivity,String>{
    List<ArticleViewActivity> findTop10ByViewedByOrderByCreatedAtDesc(String viewedBy);
    ArticleViewActivity findByArticleIdAndViewedByOrderByCreatedAtDesc(String articleId, String userId);
    List<ArticleViewActivity> findAllByArticleId(String articleId);

}
