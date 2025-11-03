package com.sprint.project.monew.article.repository;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleDtoUUID;
import com.sprint.project.monew.article.dto.ArticleRestoreResultDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.common.CursorPageResponse;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleQueryRepository {

    CursorPageResponse<ArticleDto> searchPageSortedArticle(String keyword, String interestId,
                                                           String sourceIn,
                                                           String publishDateFrom, String publishDateTo,
                                                           String orderBy, String direction,
                                                           String cursor, String after, Integer limit, UUID userId);

    List<String> searchSource();

    ArticleDtoUUID searchOne(UUID articleId, UUID userId);

    List<ArticleDto> searchForRestore(String from, String to);

    Optional<Article> findByArticleId(UUID articleId);

    List<Article> findAllArticle();

    List<Article> findTodayArticle(Instant day);

    Long getCommentCount(UUID articleId);


}
