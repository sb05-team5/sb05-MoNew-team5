package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public long countCommentsArticleId(UUID articleId) {
        return commentRepository.countByArticleId(articleId);
    }
}
