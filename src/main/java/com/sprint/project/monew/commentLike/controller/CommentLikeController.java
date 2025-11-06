package com.sprint.project.monew.commentLike.controller;

import com.sprint.project.monew.commentLike.dto.CommentLikeDto;
import com.sprint.project.monew.commentLike.service.CommentLikeService;
import com.sprint.project.monew.notification.service.NotificationService;
import com.sprint.project.monew.user.repository.UserRepository;
import com.sprint.project.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeDto> like(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        CommentLikeDto dto = commentLikeService.commentLike(commentId, userId);
        notificationService.notifyCommentLiked(commentId,dto.commentUserId(), userService.getUserIdByNickName(userId) );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> unlike(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        commentLikeService.uncommentLike(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
