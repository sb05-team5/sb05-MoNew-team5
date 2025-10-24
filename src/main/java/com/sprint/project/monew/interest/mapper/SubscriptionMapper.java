package com.sprint.project.monew.interest.mapper;

import com.sprint.project.monew.interest.dto.SubscriptionDto;
import com.sprint.project.monew.interest.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

  @Mapping(source = "interest.id", target = "interestId")
  @Mapping(source = "interest.name", target = "interestName")
  @Mapping(source = "interest.subscriberCount", target = "interestSubscriberCount")
  @Mapping(target = "interestKeywords",
      expression = "java(subscription.getInterest().getKeywords())")
  SubscriptionDto toSubscriptionDto(Subscription subscription);
}
