package com.sprint.project.monew.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CommentRegisterRequest(
        @NotNull UUID articleId,
        @NotBlank @Size(max = 1000) String content
) {}
