package com.sprint.project.monew.log.controller;

import com.sprint.project.monew.log.dto.CommentActivityDto;
import com.sprint.project.monew.log.dto.CommentLikeActivityDto;
import com.sprint.project.monew.log.service.UserActivityService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-activities")
public class UserActivityController {

  private final UserActivityService userActivityService;

  @GetMapping("{userId}")
  public ResponseEntity<Map<String, Object>> getUserActivity(@PathVariable UUID userId) {
    List<CommentActivityDto> commentsActivity = userActivityService.getComments(userId);
    List<CommentLikeActivityDto>  commentLikesActivity = userActivityService.getCommentLikes(userId);

    Map<String, Object> response = new HashMap<>();
    response.put("comments", commentsActivity);
    response.put("commentLikes", commentLikesActivity);

    return ResponseEntity.ok(response);
  }

}
