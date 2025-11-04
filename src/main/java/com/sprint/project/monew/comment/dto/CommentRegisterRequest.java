package com.sprint.project.monew.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CommentRegisterRequest(
        @NotNull UUID articleId,
        @NotNull UUID userId,
        @NotBlank
        @Size(max = 500, message = "댓글은 500자까지만 입력 가능합니다.")
        String content
) {}
