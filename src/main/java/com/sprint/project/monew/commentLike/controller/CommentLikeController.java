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

//    private final CommentLikeService commentLikeService;
//
//    @PostMapping
//    public ResponseEntity<Long> like(
//            @PathVariable UUID commentId,
//            @RequestHeader("User-Id") UUID userId
//    ) {
//        long likeCount = commentLikeService.commentLike(commentId, userId);
//        return ResponseEntity.ok(likeCount);
//    }
//
//    @DeleteMapping
//    public ResponseEntity<Long> unlike(
//            @PathVariable UUID commentId,
//            @RequestHeader("User-Id") UUID userId
//    ) {
//        long likeCount = commentLikeService.uncommentLike(commentId, userId);
//        return ResponseEntity.ok(likeCount);
//    }

}
