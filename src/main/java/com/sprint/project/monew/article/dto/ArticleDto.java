package com.sprint.project.monew.article.dto;

import com.sprint.project.monew.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


public record ArticleDto(
    UUID id,
    String source,
    String sourceUrl,
    String title,
    Instant publishDate,
    String summary,
    Long commentCount,
    Integer viewCount,
    Boolean viewedByBme
){
    @Builder(toBuilder = true)
    public static ArticleDto of(
            UUID id,
            String source,
            String sourceUrl,
            String title,
            Instant publishDate,
            String summary,
            Long commentCount,
            Integer viewCount,
            Boolean viewedByBme
    ) {
        return new ArticleDto(id, source, sourceUrl, title, publishDate, summary, commentCount, viewCount, viewedByBme);
    }

}
