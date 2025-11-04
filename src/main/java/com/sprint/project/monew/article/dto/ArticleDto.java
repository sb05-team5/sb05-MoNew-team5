package com.sprint.project.monew.article.dto;

import com.sprint.project.monew.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ArticleDto(
    String id,
    Instant createdAt,
    String source,
    String sourceUrl,
    String title,
    String publishDate,
    String summary,
    Long commentCount,
    Integer viewCount,
    Instant deleted_at,
    Boolean viewedByBme
){
    @Builder(toBuilder = true)
    public static ArticleDto of(
            String id,
            Instant createdAt,
            String source,
            String sourceUrl,
            String title,
            String publishDate,
            String summary,
            Long commentCount,
            Integer viewCount,
            Instant deleted_at,
            Boolean viewedByBme
    ) {
        return new ArticleDto(id, createdAt,source, sourceUrl, title, publishDate, summary,commentCount ,viewCount, deleted_at, viewedByBme);
    }

}
