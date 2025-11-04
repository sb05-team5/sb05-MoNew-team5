package com.sprint.project.monew.user.mapper;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toUser(UserDto userDto);

  @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
  User toUser(UserRegisterRequest userRegisterRequest);

  UserDto toUserDto(User user);
}
