package com.sprint.project.monew.log.listener;

import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.log.document.CommentActivity;
import com.sprint.project.monew.log.document.CommentLikeActivity;
import com.sprint.project.monew.log.event.CommentDeleteEvent;
import com.sprint.project.monew.log.event.CommentLikeRegisterEvent;
import com.sprint.project.monew.log.event.CommentRegisterEvent;
import com.sprint.project.monew.log.event.CommentUpdateEvent;
import com.sprint.project.monew.log.repository.CommentActivityRepository;
import com.sprint.project.monew.log.repository.CommentLikeActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CommentActivityListener {

  private final CommentActivityRepository commentActivityRepository;
  private final CommentLikeActivityRepository commentLikeActivityRepository;
  private final CommentLikeRepository commentLikeRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCommentRegister(CommentRegisterEvent event) {
    CommentActivity commentActivity = CommentActivity.builder()
        .id(event.comment().getId().toString())
        .createdAt(Instant.now())
        .articleId(event.article().getId().toString())
        .articleTitle(event.article().getTitle())
        .userId(event.user().getId().toString())
        .userName(event.user().getNickname())
        .content(event.comment().getContent())
        .likeCount(commentLikeRepository.countByComment_Id(event.comment().getId()))
        .build();

    commentActivityRepository.save(commentActivity);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCommentUpdate(CommentUpdateEvent event) {
    String commentId = event.getComment().getId().toString();

    commentActivityRepository.findById(commentId)
        .ifPresent(existing -> {
          CommentActivity updated = existing.update(
              event.getComment().getContent()
          );
          commentActivityRepository.save(updated);
        });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCommentDelete(CommentDeleteEvent event) {
    String commentId = event.getComment().getId().toString();

    commentActivityRepository.deleteById(commentId);

    System.out.println("[INFO] Deleted CommentActivity for commentId = " + commentId);
  }


  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCommentLikeRegister(CommentLikeRegisterEvent event) {
    CommentLikeActivity commentLikeActivity = CommentLikeActivity.builder()
        .createdAt(Instant.now())
        .articleId(event.article().getId().toString())
        .articleTitle(event.article().getTitle())
        .commentId(event.comment().getId().toString())
        .content(event.comment().getContent())
        .likeCount(commentLikeRepository.countByComment_Id(event.comment().getId()))
        .commentCreatedAt(event.comment().getCreatedAt())
        .userId(event.user().getId().toString())
        .userName(event.user().getNickname())
        .build();

    commentLikeActivityRepository.save(commentLikeActivity);
  }

}
