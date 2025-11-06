package com.sprint.project.monew.log.service;

import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.articleView.repository.ArticleViewRepository;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.log.document.ArticleViewActivity;
import com.sprint.project.monew.log.document.SubscriptionActivity;
import com.sprint.project.monew.log.dto.CommentActivityDto;
import com.sprint.project.monew.log.dto.CommentLikeActivityDto;
import com.sprint.project.monew.log.dto.SubscriptionActivityDto;
import com.sprint.project.monew.log.repository.ArticleViewActivityRepository;
import com.sprint.project.monew.log.repository.CommentActivityRepository;
import com.sprint.project.monew.log.repository.CommentLikeActivityRepository;
import com.sprint.project.monew.log.repository.SubscriptionActivityRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivityService {

  private final CommentActivityRepository commentActivityRepository;
  private final CommentLikeActivityRepository commentLikeActivityRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final UserRepository userRepository;
  private final SubscriptionActivityRepository subscriptionActivityRepository;
  private final ArticleViewActivityRepository  articleViewActivityRepository;

  public List<ArticleViewDto> getArticleView(UUID userId) {

    List<ArticleViewActivity> view = articleViewActivityRepository.findTop10ByViewedByOrderByCreatedAtDesc(String.valueOf(userId));
    List<ArticleViewDto> viewDto = new ArrayList<>();
    for (ArticleViewActivity v : view) {
      ArticleViewDto dto = ArticleViewDto.builder()
              .id(v.getId())
              .viewedBy(UUID.fromString(v.getViewedBy()))
              .createdAt(v.getCreatedAt())
              .articleId(UUID.fromString(v.getArticleId()))
              .source(v.getSource())
              .sourceUrl(v.getSourceUrl())
              .articleTitle(v.getArticleTitle())
              .articleViewCount(v.getArticleViewCount())
              .articleSummary(v.getArticleSummary())
              .articlePublishedDate(v.getArticlePublishedDate())
              .articleCommentCount(v.getArticleCommentCount())
              .build();
      viewDto.add(dto);
    }

    return viewDto;
  }

  public List<SubscriptionActivityDto> getSubscriptionActivity(UUID userId) {

    List<SubscriptionActivity> subs = subscriptionActivityRepository.findAllUserIdByOrderByCreatedAtDesc(String.valueOf(userId));
    List<SubscriptionActivityDto> subsDto = new ArrayList<>();
    for (SubscriptionActivity activity : subs) {
      SubscriptionActivityDto dto = SubscriptionActivityDto.builder()
              .id(activity.getId())
              .interestId(activity.getInterestId())
              .interestName(activity.getInterestName())
              .interestKeywords(activity.getInterestKeywords())
              .interestSubscripberCount(activity.getInterestSubscripberCount())
              .createdAt(activity.getCreatedAt())
              .build();
      subsDto.add(dto);
    }

    return subsDto;
  }



  public List<CommentActivityDto> getComments(UUID userId) {
    User user = validatedUserId(userId);

    return commentActivityRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId().toString()).stream()
        .map(c -> {
          int latestLikeCount = commentLikeRepository.countByComment_Id(UUID.fromString(c.getId()));

           return CommentActivityDto.builder()
              .id(c.getId())
              .createdAt(c.getCreatedAt())
              .articleId(c.getArticleId())
              .articleTitle(c.getArticleTitle())
              .userId(c.getUserId())
              .userName(c.getUserName())
              .content(c.getContent())
              .likeCount(latestLikeCount)
              .build();
        })
        .toList();
  }


  public List<CommentLikeActivityDto> getCommentLikes(UUID userId) {
    User user = validatedUserId(userId);

    return commentLikeActivityRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId().toString()).stream()
        .map(c -> CommentLikeActivityDto.builder()
            .id(c.getId())
            .createdAt(c.getCreatedAt())
            .articleId(c.getArticleId())
            .articleTitle(c.getArticleTitle())
            .commentId(c.getCommentId())
            .content(c.getContent())
            .likeCount(c.getLikeCount())
            .commentCreatedAt(c.getCommentCreatedAt())
            .userId(c.getUserId())
            .userName(c.getUserName())
            .build())
        .toList();
  }

  private User validatedUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("Not found user"));
    return user;
  }




}
