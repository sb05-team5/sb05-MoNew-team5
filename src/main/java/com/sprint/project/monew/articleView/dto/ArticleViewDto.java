package com.sprint.project.monew.articleView.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Builder(toBuilder = true)
public record ArticleViewDto(
  String id,
  UUID viewedBy,
  Instant createdAt,
  UUID articleId,
  String source,
  String sourceUrl,
  String articleTitle,
  String articlePublishedDate,
  String articleSummary,
  Long articleCommentCount,
  Integer articleViewCount
){}
