package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    UUID write(UUID articleId, UUID userId, String content);
    Page<Comment> list(Pageable pageable);
    void update(UUID commentId, UUID userId, String content);
    void softDelete(UUID commentId, UUID userId); // 논리삭제
    void hardDelete(UUID commentId, UUID userId); // 물리삭제
}
