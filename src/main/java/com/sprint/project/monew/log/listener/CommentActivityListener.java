package com.sprint.project.monew.log.listener;

import com.sprint.project.monew.log.document.CommentActivity;
import com.sprint.project.monew.log.event.CommentRegisterEvent;
import com.sprint.project.monew.log.repository.CommentActivityRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommentActivityListener {

  private final CommentActivityRepository commentActivityRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(CommentRegisterEvent event) {
    CommentActivity commentActivity = CommentActivity.builder()
        .createdAt(Instant.now())
        .articleId(event.article().getId().toString())
        .articleTitle(event.article().getTitle())
        .userId(event.user().getId().toString())
        .userName(event.user().getNickname())
        .content(event.comment().getContent())
        .likeCount(event.comment().getLikeCount())
        .build();

    commentActivityRepository.save(commentActivity);
  }
}
