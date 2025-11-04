package com.sprint.project.monew.articleBackup.repository;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleBackupRepository extends JpaRepository<ArticleBackup, UUID>,ArticleBackupQueryRepository {


}
