package com.sprint.project.monew.commentLike.mapper;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.commentLike.dto.CommentLikeDto;
import com.sprint.project.monew.commentLike.entity.CommentLike;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {
    default CommentLikeDto toDto(CommentLike like, Comment comment, long newLikeCount) {
        return new CommentLikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getCreatedAt(),
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                newLikeCount,
                comment.getCreatedAt()
        );
    }
}
