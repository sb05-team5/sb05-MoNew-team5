package com.sprint.project.monew.comment.repository;

import com.sprint.project.monew.comment.entity.Comment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comment c where c.id = : id")
    Optional<Comment> findForUpdate(@Param("id") UUID id);

//    @Query()
//    List<Comment> pageByDateDesc(@Param("articleId") UUID articleId,
//                                 @Param("createdAt") Instant createdAt,
//                                 @Param("id") UUID id,
//                                 Pageable page);
//
//    @Query()
//    List<Comment> pageByDateAsc(@Param("articleId") UUID articleId,
//                                @Param("createdAt") Instant createdAt,
//                                @Param("id") UUID id,
//                                Pageable page);
//
//    @Query()
//    List<Comment> pageByLikesDesc(@Param("articleId") UUID articleId,
//                                  @Param("likeCount") long likeCount,
//                                  @Param("id") UUID id,
//                                  Pageable page);
//
//    @Query()
//    List<Comment> pageByLikesAsc(@Param("articleId") UUID articleId,
//                                 @Param("likeCount") long likeCount,
//                                 @Param("id") UUID id,
//                                 Pageable page);

    @Query("""
        select count(c)
        from Comment c
        where c.articleId = :articleId
          and c.deletedAt is null
    """)
    long countByArticleId(@Param("articleId") UUID articleId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Comment c where c.userId in :userIds")
    void deleteAllByUserIds(List<UUID> userIds);
}
