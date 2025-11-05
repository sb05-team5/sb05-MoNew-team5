package com.sprint.project.monew.log.service;

import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.log.dto.CommentActivityDto;
import com.sprint.project.monew.log.dto.CommentLikeActivityDto;
import com.sprint.project.monew.log.repository.CommentActivityRepository;
import com.sprint.project.monew.log.repository.CommentLikeActivityRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
