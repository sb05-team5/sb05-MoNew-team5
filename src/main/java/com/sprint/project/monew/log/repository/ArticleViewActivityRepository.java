package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.ArticleViewActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ArticleViewActivityRepository extends MongoRepository<ArticleViewActivity,String>{
    List<ArticleViewActivity> findTop10ByViewedByOrderByCreatedAtDesc(UUID viewedBy);

}
