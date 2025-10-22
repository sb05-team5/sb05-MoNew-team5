package com.sprint.project.monew.interest.mapper;

import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.entity.Interest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterestMapper {

  InterestDto toInterestDto(Interest interest);
}
