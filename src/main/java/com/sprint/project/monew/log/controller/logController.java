package com.sprint.project.monew.log.controller;


import com.sprint.project.monew.log.document.SubscriptionActivity;
import com.sprint.project.monew.log.dto.SubscriptionActivityDto;
import com.sprint.project.monew.log.repository.SubscriptionActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log")
public class logController {
    private final SubscriptionActivityRepository subscriptionActivityRepository;


    @GetMapping("")
    public List<SubscriptionActivityDto> findAllByOrderByCreatedAtDesc() {
        List<SubscriptionActivity> activities = subscriptionActivityRepository.findAllByOrderByCreatedAtDesc();
        List<SubscriptionActivityDto> activitiesDto = new ArrayList<>();
        for (SubscriptionActivity activity : activities) {
            SubscriptionActivityDto dto = SubscriptionActivityDto.builder()
                    .id(activity.getId())
                    .interestId(activity.getInterestId())
                    .interestName(activity.getInterestName())
                    .interestKeywords(activity.getInterestKeywords())
                    .interestSubscripberCount(activity.getInterestSubscripberCount())
                    .createdAt(activity.getCreatedAt())
                    .build();
            activitiesDto.add(dto);
        }
        return activitiesDto.subList(0, Math.min(10, activitiesDto.size()));
    }
}
