package com.sprint.project.monew.comment.controller;

import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.dto.CommentRegisterRequest;
import com.sprint.project.monew.comment.dto.CommentUpdateRequest;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

//    // 댓글 목록 조회
//    @GetMapping("/comments")
//    public ResponseEntity<Page<CommentDto>> list(Pageable pageable) {
//        Page<Comment> page = commentService.list(pageable);
//        return ResponseEntity.ok(page.map(commentMapper::toDto));
//    }
//
//    // 댓글 등록
//    @PostMapping("/comments")
//    public ResponseEntity<Void> register(
//            @RequestHeader("User-Id") UUID userId,
//            @Valid @RequestBody CommentRegisterRequest req
//    ) {
//        UUID id = commentService.write(req.articleId(), userId, req.content());
//        return ResponseEntity.created(URI.create("/api/comments/" + id)).build();
//    }
//
//    // 댓글 정보 수정
//    @PatchMapping("/comments/{commentId}")
//    public ResponseEntity<Void> update(
//            @PathVariable UUID commentId,
//            @RequestHeader("User-Id") UUID userId,
//            @Valid @RequestBody CommentUpdateRequest req
//    ) {
//        commentService.update(commentId, userId, req.content());
//        return ResponseEntity.noContent().build();
//    }
//
//    // 댓글 논리삭제
//    @DeleteMapping("/comments/{commentId}")
//    public ResponseEntity<Void> softDelete(
//            @PathVariable UUID commentId,
//            @RequestHeader("User-Id") UUID userId
//    ) {
//        commentService.softDelete(commentId, userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 댓글 물리삭제
//    @DeleteMapping("/comments/{commentId}/hard")
//    public ResponseEntity<Void> hardDelete(
//            @PathVariable UUID commentId,
//            @RequestHeader("User-Id") UUID userId
//    ) {
//        commentService.hardDelete(commentId, userId);
//        return ResponseEntity.noContent().build();
//    }
}
