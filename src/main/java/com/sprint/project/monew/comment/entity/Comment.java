package com.sprint.project.monew.comment.entity;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.common.BaseEntity;
import com.sprint.project.monew.user.entity.User;
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

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id", nullable = false, updatable = false)
        private UUID id;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "article_id", nullable = false)
        private Article article;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false, length = 1000)
        private String content;

        @Column(name = "like_count", nullable = false)
        private int likeCount = 0;

        @Column(name = "deleted_at")
        private Instant deletedAt;

//        @Version
//        private Long version;

        public static Comment create(Article article, User user, String content) {
                Comment comment = new Comment();
                comment.article = article;
                comment.user = user;
                comment.setContentInternal(content);
                comment.deletedAt = null;
                comment.likeCount = 0;
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

        public void increaseLike() { this.likeCount++; }

        public void decreaseLike() { this.likeCount = Math.max(0, this.likeCount - 1); }

        private void setContentInternal(String content) {
                if (content == null || content.isBlank() || content.length() > 1000) {
                        throw new IllegalArgumentException("댓글은 1000자 이내여야 합니다.");
                }
                this.content = content;
        }
}

