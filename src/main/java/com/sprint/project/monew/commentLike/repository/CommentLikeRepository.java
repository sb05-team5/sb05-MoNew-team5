package com.sprint.project.monew.commentLike.repository;

import com.sprint.project.monew.commentLike.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    boolean existsByComment_IdAndUser_Id(UUID commentId, UUID userId);

    Optional<CommentLike> findByComment_IdAndUser_Id(UUID commentId, UUID userId);

    int countByComment_Id(UUID commentId);

    void deleteByComment_IdAndUser_Id(UUID commentId, UUID userId);

    @Query("""
           select cl.comment.id, count(cl)
           from CommentLike cl
           where cl.comment.id in :commentIds
           group by cl.comment.id
           """)
    List<Object[]> countByCommentIdsRaw(Collection<UUID> commentIds);

    @Query("select cl.comment.id as commentId, count(cl.id) as cnt " +
            "from CommentLike cl " +
            "where cl.comment.id in :commentIds " +
            "group by cl.comment.id")
    List<Object[]> countGroupByCommentIds(@Param("commentIds") Collection<UUID> commentIds);

    @Query("select cl.comment.id " +
            "from CommentLike cl " +
            "where cl.user.id = :userId and cl.comment.id in :commentIds")
    Set<UUID> findLikedCommentIds(@Param("userId") UUID userId,
                                  @Param("commentIds") Collection<UUID> commentIds);
}
