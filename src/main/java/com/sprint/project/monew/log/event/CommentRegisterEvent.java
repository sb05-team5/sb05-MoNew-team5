package com.sprint.project.monew.log.event;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.user.entity.User;

public record CommentRegisterEvent(
    Comment comment,
    Article article,
    User user
) {

}
