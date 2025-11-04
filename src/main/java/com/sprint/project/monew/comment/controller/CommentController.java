package com.sprint.project.monew.comment.controller;

import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.dto.CommentRegisterRequest;
import com.sprint.project.monew.comment.dto.CommentUpdateRequest;
import com.sprint.project.monew.comment.service.CommentService;
import com.sprint.project.monew.common.CursorPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final ArticleService articleService;

    // 댓글 목록 조회
    @GetMapping("/comments")
    public ResponseEntity<CursorPageResponse<CommentDto>> listByArticle(
            @RequestParam UUID articleId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String after,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        CursorPageResponse<CommentDto> page =
                commentService.pageByArticle(articleId, orderBy, direction, cursor, limit);

        return ResponseEntity.ok(page);
    }

    // 댓글 등록
    @PostMapping("/comments")
    public ResponseEntity<CommentDto> register(
            @Valid @RequestBody CommentRegisterRequest req
    ) {
        UUID id = commentService.create(req.articleId(), req.userId(), req.content());
        CommentDto dto = commentService.findDtoById(id);

        return ResponseEntity.created(URI.create("/api/comments/" + id)).body(dto);
    }

    // 댓글 정보 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID userId,
            @Valid @RequestBody CommentUpdateRequest req
    ) {
        CommentDto dto = commentService.update(commentId, userId, req.content());
        return ResponseEntity.ok(dto);
    }

    // 댓글 논리삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable UUID commentId,
            @RequestHeader(value = "MoNew-Request-User-ID", required = false) UUID userIdHeader,
            @RequestParam(value = "userId",required = false) UUID userIdParam
    ) {
        UUID userId = (userIdHeader != null) ? userIdHeader : userIdParam;
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId가 필요합니다.");
        }
        UUID articleId = commentService.getArticleId(commentId);
        commentService.softDelete(commentId, userId);
        articleService.decrementCommentCount(articleId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 물리삭제
    @DeleteMapping("/comments/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID commentId,
            @RequestHeader(value = "Monew-Request-User-ID", required = false) UUID userIdHeader,
            @RequestParam(value = "userId", required = false) UUID userIdParam
    ) {
        UUID userId = (userIdHeader != null) ? userIdHeader : userIdParam;
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId가 필요합니다. (header 또는 ?userId=)");
        }
        UUID articleId = commentService.getArticleId(commentId);
        commentService.hardDelete(commentId, userId);
        articleService.decrementCommentCount(articleId);
        return ResponseEntity.noContent().build();
    }
}
