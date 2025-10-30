package com.sprint.project.monew.comment.repository;

import com.sprint.project.monew.comment.entity.Comment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository
        extends JpaRepository<Comment, UUID>, CommentQueryRepository {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select c from Comment c where c.id = : id")
//    Optional<Comment> findForUpdate(@Param("id") UUID id);
//
//    List<Comment> findOrderByDate(
//            UUID articleId, boolean sort, Instant dateCursor, UUID idCursor, int limit
//    );
//
//    List<Comment> findOrderByLikes(
//            UUID articleId, boolean sort, Integer likeCursor, UUID idCursor, int limit
//    );
}
