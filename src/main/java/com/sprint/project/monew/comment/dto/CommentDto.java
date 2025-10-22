package com.sprint.project.monew.comment.dto;

import java.util.UUID;

public record CommentDto(
        UUID id,
        String content
) {}
