package com.sprint.project.monew.log.event;

import com.sprint.project.monew.comment.entity.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentUpdateEvent extends ApplicationEvent {
  private final Comment comment;

  public CommentUpdateEvent(Object source, Comment comment) {
    super(source);
    this.comment = comment;
  }
}
