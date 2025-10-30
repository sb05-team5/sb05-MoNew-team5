package com.sprint.project.monew.commentLike.repository;

import com.sprint.project.monew.commentLike.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

//    boolean existsByComment_IdAndUser_Id(UUID commentId, UUID userId);
//
//    Optional<CommentLike> findByComment_IdAndUser_Id(UUID commentId, UUID userId);
//
//    int countByComment_Id(UUID commentId);
//
//    void deleteByComment_IdAndUser_Id(UUID commentId, UUID userId);
}
