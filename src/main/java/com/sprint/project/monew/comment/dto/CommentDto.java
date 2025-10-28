package com.sprint.project.monew.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID articleId,
        UUID userId,
        String content,
        int likeCount,
        Instant createdAt
) {}
