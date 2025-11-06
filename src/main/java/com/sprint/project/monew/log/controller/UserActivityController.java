package com.sprint.project.monew.log.controller;

import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.log.document.SubscriptionActivity;
import com.sprint.project.monew.log.dto.CommentActivityDto;
import com.sprint.project.monew.log.dto.CommentLikeActivityDto;
import com.sprint.project.monew.log.dto.SubscriptionActivityDto;
import com.sprint.project.monew.log.repository.SubscriptionActivityRepository;
import com.sprint.project.monew.log.service.UserActivityService;

import java.util.*;

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
    List<SubscriptionActivityDto> subscriptionActivity = userActivityService.getSubscriptionActivity(userId);
    List<ArticleViewDto> articleViewActivity = userActivityService.getArticleView(userId);



    Map<String, Object> response = new HashMap<>();
    response.put("comments", commentsActivity);
    response.put("commentLikes", commentLikesActivity);
    response.put("subscriptions", subscriptionActivity);
    response.put("articleViews", articleViewActivity);


    return ResponseEntity.ok(response);
  }

}
