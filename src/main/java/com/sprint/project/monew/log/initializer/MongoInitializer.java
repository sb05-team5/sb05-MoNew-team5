package com.sprint.project.monew.log.initializer;


import com.sprint.project.monew.log.repository.ArticleViewActivityRepository;
import com.sprint.project.monew.log.repository.SubscriptionActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class MongoInitializer {
    private final SubscriptionActivityRepository subscriptionActivityRepository;
    private final ArticleViewActivityRepository articleViewActiviryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 컬렉션 삭제
        subscriptionActivityRepository.deleteAll();
        articleViewActiviryRepository.deleteAll();



    }

}
