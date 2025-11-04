package com.sprint.project.monew.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CursorPageResponseArticleDto {
    List<ArticleDto> content;
    private String nextCursor;
    private Instant nextAfter;
    private int size;
    private long totalElements;
    private boolean hasNext;
}
