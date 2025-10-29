package com.sprint.project.monew.commentLike.repository;

import com.sprint.project.monew.commentLike.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

//    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);
//
//    Optional<CommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);
//
//    long countByCommentId(UUID commentId);
//
//    void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
