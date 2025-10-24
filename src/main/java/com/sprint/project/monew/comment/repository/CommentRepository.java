package com.sprint.project.monew.comment.repository;

import com.sprint.project.monew.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // 논리삭제되지 않은 댓글만 조회
    Page<Comment> findByDeletedFalse(Pageable pageable);

    // 논리삭제되지 않은 특정 댓글 조회
    Optional<Comment> findByIdAndDeletedFalse(UUID id);
}
