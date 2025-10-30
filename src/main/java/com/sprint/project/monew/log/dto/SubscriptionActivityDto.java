package com.sprint.project.monew.log.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
@Builder(toBuilder = true)
public record SubscriptionActivityDto(
        String id,
        Instant createdAt,
        String interestId,
        String interestName,
        List<String> interestKeywords,
        Integer interestSubscripberCount
) {
}
