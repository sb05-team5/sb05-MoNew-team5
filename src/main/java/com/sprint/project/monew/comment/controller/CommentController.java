package com.sprint.project.monew.comment.controller;

import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.dto.CommentRegisterRequest;
import com.sprint.project.monew.comment.dto.CommentUpdateRequest;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.service.CommentService;
import com.sprint.project.monew.common.CursorPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final ArticleService articleService;

    // 댓글 목록 조회
    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<CursorPageResponse<CommentDto>> listByArticle(
            @PathVariable UUID articleId,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        SortKey sortKey = SortKey.from(sort);
        SortDir sortDir = SortDir.from(order);

        int pageSize = normalizedSize(size);

        CursorPageResponse<CommentDto> page =
                commentService.pageByArticle(articleId, sortKey.value, sortDir.value, cursor, pageSize);

        return ResponseEntity.ok(page);
    }

    // 댓글 등록
    @PostMapping("/comments")
    public ResponseEntity<Void> register(
            @RequestHeader("User-Id") UUID userId,
            @Valid @RequestBody CommentRegisterRequest req
    ) {
        UUID id = commentService.create(req.articleId(), userId, req.content());
        if(id != null) {
            articleService.incrementCommentCount(req.articleId());
        }

        return ResponseEntity.created(URI.create("/api/comments/" + id)).build();
    }

    // 댓글 정보 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable UUID commentId,
            @RequestHeader("User-Id") UUID userId,
            @Valid @RequestBody CommentUpdateRequest req
    ) {
        commentService.update(commentId, userId, req.content());
        return ResponseEntity.noContent().build();
    }

    // 댓글 논리삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable UUID commentId,
            @RequestHeader("User-Id") UUID userId
    ) {
        commentService.softDelete(commentId, userId);
        UUID articleId=commentService.getArticleId(commentId);
        articleService.decrementCommentCount(articleId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 물리삭제
    @DeleteMapping("/comments/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID commentId,
            @RequestHeader("User-Id") UUID userId
    ) {
        commentService.hardDelete(commentId, userId);
        UUID articleId=commentService.getArticleId(commentId);
        articleService.decrementCommentCount(articleId);
        return ResponseEntity.noContent().build();
    }

    private int normalizedSize(int size) {
        if (size <= 0) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }

    public enum SortKey {
        DATE("date"),
        LIKES("likes");

        public final String value;

        SortKey(String value) {
            this.value = value;
        }

        static SortKey from(String raw) {
            if (raw == null) {
                return DATE;
            }

            return switch (raw.toLowerCase()) {
                case "date" -> DATE;
                case "likes", "like", "likecount" -> LIKES;
                default -> throw new IllegalArgumentException("잘못된 기준입니다." + raw);
            };
        }
    }

    public enum SortDir {
        ASC("asc"),
        DESC("desc");

        public final String value;

        SortDir(String value) {
            this.value = value;
        }

        static SortDir from(String raw) {
            if (raw == null) {
                return DESC;
            }
            return switch (raw.toLowerCase()) {
                case "asc" -> ASC;
                case "desc" -> DESC;
                default -> throw new IllegalArgumentException("잘못된 기준입니다." + raw);
            };
        }
    }
}
