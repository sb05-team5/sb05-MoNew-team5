package com.sprint.project.monew.commentLike.controller;

import com.sprint.project.monew.commentLike.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/{commentId}/likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @GetMapping
    public ResponseEntity<LikeStatusResponse> getStatus(
            @PathVariable UUID commentId,
            @RequestHeader(value = "MoNew-Request-User-ID", required = false) UUID userId
    ) {
        int likeCount = commentLikeService.getLikeCount(commentId);
        boolean liked = (userId != null) && commentLikeService.isLikedByUser(commentId, userId);
        return ResponseEntity.ok(new LikeStatusResponse(likeCount, liked));
    }

    @PostMapping
    public ResponseEntity<Integer> like(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        int likeCount = commentLikeService.commentLike(commentId, userId);
        return ResponseEntity.ok(likeCount);
    }

    @DeleteMapping
    public ResponseEntity<Integer> unlike(
            @PathVariable UUID commentId,
            @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        int likeCount = commentLikeService.uncommentLike(commentId, userId);
        return ResponseEntity.ok(likeCount);
    }

    public record LikeStatusResponse(int likeCount, boolean liked) {}
}
