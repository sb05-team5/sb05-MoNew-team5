package com.sprint.project.monew.comment.mapper;

import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "id")
    CommentDto toDto(Comment comment);
}
