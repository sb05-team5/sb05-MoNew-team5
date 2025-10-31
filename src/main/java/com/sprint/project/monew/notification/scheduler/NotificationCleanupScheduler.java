package com.sprint.project.monew.notification.scheduler;


import com.sprint.project.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationRepository repo;

    /** 매일 03:00 KST */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void cleanup() {
        Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
        long deleted = repo.deleteByConfirmedIsTrueAndUpdatedAtBefore(threshold);
        log.info("[NotificationCleanupScheduler] Deleted {} confirmed notifications (updatedAt before {}).",
                deleted, threshold);
    }
}