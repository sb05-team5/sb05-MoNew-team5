package com.sprint.project.monew.comment.mapper;

import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.CommentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toCommentDto(CommentEntity comment);
}
