package com.sprint.project.monew.user.batch;

import com.sprint.project.monew.articleView.repository.ArticleViewRepository;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.interest.repository.SubscriptionRepository;
import com.sprint.project.monew.notification.repository.NotificationRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserBatchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserCleanUpJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  private final UserBatchRepository userBatchRepository;
  private final CommentRepository commentRepository;
  private final ArticleViewRepository articleViewRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationRepository notificationRepository;

  @Bean
  public Job userCleanUpJob() {
    return new JobBuilder("UserCleanUpJob", jobRepository)
        .start(commentCleanupStep())
        .next(articleViewCleanupStep())
        .next(subscriptionCleanupStep())
        .next(notificationCleanupStep())
        .next(userCleanupStep())
        .build();
  }

  private Step commentCleanupStep() {
    return new StepBuilder("commentCleanupStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanupReader())
        .writer(chunk -> {
          List<UUID> userIds = extractUserIds(chunk);
          commentRepository.deleteAllByUserIds(userIds);
          log.info("[UserCleanUpJob][commentCleanupStep] 댓글 삭제 완료 - {}명", userIds.size());
        })
        .build();
  }

  private Step articleViewCleanupStep() {
    return new StepBuilder("articleViewCleanupStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanupReader())
        .writer(chunk -> {
          List<UUID> userIds = extractUserIds(chunk);
          articleViewRepository.deleteAllByUserIds(userIds);
          log.info("[UserCleanUpJob][articleViewCleanupStep] 기사조회 삭제 완료 - {}명", userIds.size());
        })
        .build();
  }

  private Step subscriptionCleanupStep() {
    return new StepBuilder("subscriptionCleanupStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanupReader())
        .writer(chunk -> {
          List<UUID> userIds = extractUserIds(chunk);
          subscriptionRepository.deleteAllByUserIds(userIds);
          log.info("[UserCleanUpJob][subscriptionCleanupStep] 구독 삭제 완료 - {}명", userIds.size());
        })
        .build();
  }

  private Step notificationCleanupStep() {
    return new StepBuilder("notificationCleanupStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanupReader())
        .writer(chunk -> {
          List<UUID> userIds = extractUserIds(chunk);
          notificationRepository.deleteAllByUserIds(userIds);
          log.info("[UserCleanUpJob][notificationCleanupStep] 알림 삭제 완료 - {}명", userIds.size());
        })
        .build();
  }

  @Bean
  public Step userCleanupStep() {
    return new StepBuilder("userCleanUpStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanupReader())
        .writer(userCleanupWriter())
        .build();
  }


  @Bean
  @StepScope
  public ItemReader<User> userCleanupReader() {
    Instant threshold = Instant.now().minus(1, ChronoUnit.DAYS);
    List<User> markedUser = userBatchRepository.findAllMarkedDeletion(threshold);
    log.info("[UserCleanUpJob] 삭제 대상 유저 조회 완료 - {}명", markedUser.size());
    return new ListItemReader<>(markedUser);
  }

  @Bean
  public ItemWriter<? super User> userCleanupWriter() {
    return chunk -> {
      List<User> users = new ArrayList<>(chunk.getItems());
      userBatchRepository.deleteAllInBatch(users);
      log.info("[UserCleanUpJob][userCleanupStep] 유저 물리삭제 완료 - {}명", users.size());
    };
  }

  private List<UUID> extractUserIds(Chunk<? extends User> chunk) {
    return chunk.getItems().stream()
        .map(User::getId)
        .toList();
  }
}
