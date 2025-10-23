package com.sprint.project.monew.interest.mapper;

import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.entity.Interest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterestMapper {

  @Mapping(target = "keywords", expression = "java(java.util.Arrays.asList(interest.getKeyword().split(\",\")))")
  InterestDto toDto(Interest interest);

  @Mapping(target = "keyword", expression = "java(String.join(\",\", req.keywords()))")
  Interest toEntity(InterestRegisterRequest req);
}