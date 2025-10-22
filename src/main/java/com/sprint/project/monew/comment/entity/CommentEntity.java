package com.sprint.project.monew.comment.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table( name = "comment")
public class CommentEntity extends BaseEntity {

        // 댓글이 달린 기사
        @Column(name = "article_id", columnDefinition = "uuid", nullable = false)
        private UUID articleId;

        // 댓글 작성자
        @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
        private UUID userId;

        @Size(max = 1000)
        @Column(name = "content", nullable = false, length = 1000)
        @Comment("댓글 내용")
        private String content;

        @Column(name = "like_count", nullable = false)
        private int likeCount;

        public static CommentEntity create(UUID articleId, UUID userId, String content) {
                return CommentEntity.builder()
                        .articleId(articleId)
                        .userId(userId)
                        .content(content.trim())
                        .likeCount(0)
                        .build();
        }

        public void editContent(String newContent) {
                this.content = newContent.trim();
        }

        public void increaseLike() { this.likeCount++; }
        public void decreaseLike() { if (this.likeCount > 0) this.likeCount--; }
}

