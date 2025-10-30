package com.sprint.project.monew.comment.repository;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.common.CursorPageResponse;

import java.util.UUID;

public interface CommentQueryRepository {

    int countByArticleId(UUID articleId);

    CursorPageResponse<Comment> pageByArticleSorted(
            UUID articleId,
            String orderBy,
            String direction,
            String after,
            Integer limit
    );
}
