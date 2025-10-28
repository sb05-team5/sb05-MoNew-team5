package com.sprint.project.monew.article.repository;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.common.CursorPageResponse;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface ArticleQueryRepository {

    CursorPageResponse<ArticleDto> searchPageSortedArticle(String keyword, String interestId,
                                                           String sourceIn,
                                                           String publishDateFrom, String publishDateTo,
                                                           String orderBy, String direction,
                                                           String cursor, String after, Integer limit, UUID userId);
}
