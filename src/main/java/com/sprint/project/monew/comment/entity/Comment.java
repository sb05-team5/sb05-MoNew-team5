package com.sprint.project.monew.comment.entity;

import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Entity
public class Comment extends BaseEntity {

        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(nullable = false)
        private UUID articleId;

        @Column(nullable = false)
        private UUID userId;

        @Column(nullable = false, length = 1000)
        private String content;

        @Column(nullable = false)
        private long likeCount = 0;

        @Column()
        private Instant deletedAt;

        @Version
        private Long version;

        public static Comment create(UUID articleId, UUID userId, String content) {
                Comment comment = new Comment();
                comment.articleId = articleId;
                comment.userId = userId;
                comment.setContentInternal(content);
                comment.deletedAt = null;
                comment.likeCount = 0;
                return comment;
        }

        public void update(String newContent) {
                setContentInternal(newContent);
        }

        // 논리삭제 : 삭제
        public void softDelete() {
                this.deletedAt = Instant.now();
        }

        // 논리삭제 : 복원
        // public void restore() {}

        public void increaseLike() { this.likeCount++; }

        public void decreaseLike() { this.likeCount = Math.max(0, this.likeCount - 1); }

        private void setContentInternal(String content) {
                if (content == null || content.isEmpty() || content.length() > 1000) {
                        throw new IllegalArgumentException("댓글은 1000자 이내여야 합니다.");
                }
                this.content = content;
        }
}

