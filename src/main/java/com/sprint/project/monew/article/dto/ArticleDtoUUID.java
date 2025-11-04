package com.sprint.project.monew.article.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public record ArticleDtoUUID (
        UUID id,
        Instant createdAt,
        String source,
        String sourceUrl,
        String title,
        String publishDate,
        String summary,
        Integer commentCount,
        Integer viewCount,
        Instant deleted_at,
        Boolean viewedByBme
){

    @Builder(toBuilder = true)
    public static ArticleDtoUUID of(
            UUID id,
            Instant createdAt,
            String source,
            String sourceUrl,
            String title,
            String publishDate,
            String summary,
            Integer commentCount,
            Integer viewCount,
            Instant deleted_at,
            Boolean viewedByBme
    ) {
        return new ArticleDtoUUID(id, createdAt,source, sourceUrl, title, publishDate, summary, commentCount, viewCount, deleted_at, viewedByBme);
    }

}