package com.sprint.project.monew.comment.mapper;

import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userNickname", source = "user.nickname")
    @Mapping(target = "likeCount", expression = "java(0L)")
    @Mapping(target = "likedByMe", expression = "java(false)")
    CommentDto toDto(Comment comment);

    default CommentDto toDtoWithCounts(Comment comment, long likeCount) {
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                likeCount,
                false,
                comment.getCreatedAt()
        );
    }
}
