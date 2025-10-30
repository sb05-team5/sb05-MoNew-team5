package com.sprint.project.monew.log.dto;

import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommentActivityDto(
    String id,
    Instant createdAt,
    String articleId,
    String articleTitle,
    String userId,
    String userName,
    String content,
    int likeCount
) { }
