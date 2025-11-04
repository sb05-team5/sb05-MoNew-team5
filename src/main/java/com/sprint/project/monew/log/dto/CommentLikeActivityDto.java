package com.sprint.project.monew.log.dto;

import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommentLikeActivityDto(
    String id,
    Instant createdAt,
    String articleId,
    String articleTitle,
    String commentId,
    String content,
    int likeCount,
    Instant commentCreatedAt,
    String userId,
    String userName
) { }
