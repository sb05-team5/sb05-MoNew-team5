package com.sprint.project.monew.article.dto;

import com.sprint.project.monew.common.BaseEntity;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ArticleDto(
    UUID id,
    String sourceUrl,
    String source,
    String title,
    String publishDate,
    String summary,
    int commentCount,
    int viewCount,
    Boolean viewedByBme
){}
