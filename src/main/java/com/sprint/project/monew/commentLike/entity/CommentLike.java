package com.sprint.project.monew.commentLike.entity;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.common.BaseEntity;
import com.sprint.project.monew.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "comment_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "comment_user",
                columnNames = {"comment_id", "user_id"}))
public class CommentLike extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static CommentLike create(Comment comment, User user) {
        CommentLike like = CommentLike.builder().comment(comment).user(user).build();
        like.createdAt = Instant.now();

        return like;
    }
}
