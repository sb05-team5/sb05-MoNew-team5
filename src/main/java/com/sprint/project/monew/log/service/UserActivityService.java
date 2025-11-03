package com.sprint.project.monew.log.service;

import com.sprint.project.monew.log.document.CommentActivity;
import com.sprint.project.monew.log.dto.CommentActivityDto;
import com.sprint.project.monew.log.repository.CommentActivityRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityService {

  private final CommentActivityRepository commentActivityRepository;
  private final UserRepository userRepository;

  public List<CommentActivityDto> getComments(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("Not found user"));

    return commentActivityRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId().toString()).stream()
        .map(c -> CommentActivityDto.builder()
            .id(c.getId())
            .createdAt(c.getCreatedAt())
            .articleId(c.getArticleId())
            .articleTitle(c.getArticleTitle())
            .userId(c.getUserId())
            .userName(c.getUserName())
            .content(c.getContent())
            .likeCount(c.getLikeCount())
            .build())
        .toList();
  }



}
