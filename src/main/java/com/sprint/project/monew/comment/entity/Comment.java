package com.sprint.project.monew.comment.entity;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.common.BaseEntity;
import com.sprint.project.monew.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Entity
public class Comment extends BaseEntity {

        @Column(name = "deleted_at")
        private Instant deletedAt;

        @Column(name = "content", nullable = false, length = 500)
        private String content;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "article_id", nullable = false)
        private Article article;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        public static Comment create(Article article, User user, String content) {
                Comment comment = new Comment();
                comment.article = article;
                comment.user = user;
                comment.setContentInternal(content);
                comment.deletedAt = null;
                comment.createdAt = Instant.now();
                return comment;
        }

        public void update(String newContent) {
                setContentInternal(newContent);
        }

        // 논리삭제 : 삭제
        public void softDelete() {
                if(deletedAt == null) {
                        deletedAt = Instant.now();
                }
        }

        // 논리삭제 : 복원
        // public void restore() {}

        private void setContentInternal(String content) {
                if (content == null || content.isBlank() || content.length() > 500) {
                        throw new IllegalArgumentException("댓글은 500자 이내여야 합니다.");
                }
                this.content = content;
        }
}

