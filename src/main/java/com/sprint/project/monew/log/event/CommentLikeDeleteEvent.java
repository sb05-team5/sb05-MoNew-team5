package com.sprint.project.monew.log.event;

import com.sprint.project.monew.commentLike.entity.CommentLike;
import org.springframework.context.ApplicationEvent;

public class CommentLikeDeleteEvent extends ApplicationEvent {
  private final CommentLike commentLike;

  public CommentLikeDeleteEvent(Object source, CommentLike commentLike) {
    super(source);
    this.commentLike = commentLike;
  }

  public CommentLike getCommentLike() {
    return commentLike;
  }
}
