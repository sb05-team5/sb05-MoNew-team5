package com.sprint.project.monew.articleView.repository;

import com.sprint.project.monew.articleView.entity.ArticleView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID>,ArticleViewQueryRepository {

  @Modifying(clearAutomatically = true,  flushAutomatically = true)
  @Query("delete from ArticleView av where av.user.id in :userIds")
  void deleteAllByUserIds(List<UUID> userIds);
}
