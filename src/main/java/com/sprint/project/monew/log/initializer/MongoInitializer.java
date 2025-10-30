package com.sprint.project.monew.log.initializer;


import com.sprint.project.monew.log.repository.SubscriptionActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoInitializer {
    private final SubscriptionActivityRepository subscriptionActivityRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 컬렉션 삭제
        subscriptionActivityRepository.deleteAll();


    }

}
