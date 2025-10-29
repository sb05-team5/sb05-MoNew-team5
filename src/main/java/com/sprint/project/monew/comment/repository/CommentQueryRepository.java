package com.sprint.project.monew.comment.repository;

import java.util.UUID;

public interface CommentQueryRepository {

    long countActiveByArticleId(UUID articleId);
    // 유저(임시) long countActiveByUserId(UUID userId);
}
