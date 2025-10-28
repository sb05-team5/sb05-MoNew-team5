package com.sprint.project.monew.commentLike.service;

import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.entity.CommentLike;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public long commentLike(UUID commentId,UUID userId) {

        var comment = commentRepository.findForUpdate(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (comment.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 댓글입니다.");
        }

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return comment.getLikeCount();
        }

        try {
            commentLikeRepository.save(CommentLike.create(commentId, userId));
        } catch (DataIntegrityViolationException e) {
            return comment.getLikeCount();
        }
        comment.increaseLike();
        return comment.getLikeCount();
    }

    @Transactional
    public long uncommentLike(UUID commentId, UUID userId) {
        var comment = commentRepository.findForUpdate(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        comment.decreaseLike();
        return comment.getLikeCount();
    }

    @Transactional
    public void toggleCommentLike(UUID commentId, UUID userId) {
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            uncommentLike(commentId, userId);
        } else {
            commentLike(commentId, userId);
        }
    }
}
