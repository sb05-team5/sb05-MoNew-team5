package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public UUID writeComment(UUID articleId, UUID userId, String content) {
        Comment comment = Comment.create(articleId, userId, content);
        return commentRepository.save(comment).getId();
    }
}
