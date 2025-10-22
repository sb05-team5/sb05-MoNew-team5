package com.sprint.project.monew.subscription.mapper;

import com.sprint.project.monew.subscription.dto.SubscriptionDto;
import com.sprint.project.monew.subscription.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

  @Mapping(source = "interest.id", target = "interestId")
  @Mapping(source = "interest.name", target = "interestName")
  @Mapping(source = "interest.subscriberCount", target = "interestSubscriberCount")
  @Mapping(target = "interestKeywords",
      expression = "java(java.util.Arrays.asList(subscription.getInterest().getKeyword().split(\",\")))")
  SubscriptionDto toSubscriptionDto(Subscription subscription);
}
