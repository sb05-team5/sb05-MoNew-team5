package com.sprint.project.monew.log.event;

import com.sprint.project.monew.comment.entity.Comment;
import org.springframework.context.ApplicationEvent;

public class CommentDeleteEvent extends ApplicationEvent {
  private final Comment comment;

  public CommentDeleteEvent(Object source, Comment comment) {
    super(source);
    this.comment = comment;
  }

  public Comment getComment() {
    return comment;
  }

}
