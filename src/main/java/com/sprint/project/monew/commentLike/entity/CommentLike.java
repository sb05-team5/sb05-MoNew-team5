package com.sprint.project.monew.commentLike.entity;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.common.BaseEntity;
import com.sprint.project.monew.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static CommentLike create(Comment comment, User user) {
        return CommentLike.builder().comment(comment).user(user).build();
    }
}
