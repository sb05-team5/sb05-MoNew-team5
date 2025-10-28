package com.sprint.project.monew.commentLike.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class CommentLike extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID commentId;

    @Column(nullable = false)
    private UUID userId;

    public static CommentLike create(UUID commentId, UUID userId) {
        CommentLike commentLike = new CommentLike();
        commentLike.commentId = commentId;
        commentLike.userId = userId;
        return commentLike;
    }
}
