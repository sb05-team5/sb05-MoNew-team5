package com.sprint.project.monew.comment.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

        @Column(name = "article_id", columnDefinition = "uuid", nullable = false)
        private UUID articleId;

        @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
        private UUID userId;

        @NotBlank
        @Size(max = 1000)
        @Column(name = "content", nullable = false, length = 1000)
        @org.hibernate.annotations.Comment("댓글 내용")
        private String content;

        @Column(name = "like_count", nullable = false)
        private int likeCount;

        // deleted가 true면 논리삭제 상태
        @Column(name = "deleted", nullable = false)
        private boolean deleted;

        public static Comment create(UUID articleId, UUID userId, String content) {
                return Comment.builder()
                        .articleId(articleId)
                        .userId(userId)
                        .content(content.trim())
                        .likeCount(0)
                        .build();
        }

        public void update(String newContent) {
                if (newContent != null) {
                        this.content = newContent.trim();
                }
        }

        // 논리삭제 : 삭제
        public void softDelete() {
                this.deleted = true;
        }

        // 논리삭제 : 복원
        public void restore() {
                this.deleted = false;
        }

        public void increaseLike() { this.likeCount++; }
        public void decreaseLike() { if (this.likeCount > 0) this.likeCount--; }
}

