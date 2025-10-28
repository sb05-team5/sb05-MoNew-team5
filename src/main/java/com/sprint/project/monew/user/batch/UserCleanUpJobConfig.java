package com.sprint.project.monew.user.batch;

import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserBatchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class UserCleanUpJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final UserBatchRepository userBatchRepository;

  @Bean
  public Job userCleanUpJob() {
    return new JobBuilder("UserCleanUpJob", jobRepository)
        .start(userCleanUpStep())
        .build();
  }

  @Bean
  public Step userCleanUpStep() {
    return new StepBuilder("userCleanUpStep", jobRepository)
        .<User, User>chunk(100, transactionManager)
        .reader(userCleanUpReader())
        .writer(userCleanUpWriter())
        .build();
  }


  @Bean
  public ItemReader<User> userCleanUpReader() {
    Instant threshold = Instant.now().minus(1, ChronoUnit.DAYS);
    List<User> markedUser = userBatchRepository.findAllMarkedDeletion(threshold);
    return new ListItemReader<>(markedUser);
  }

  @Bean
  public ItemWriter<? super User> userCleanUpWriter() {
    return chunk -> {
      List<User> users = new ArrayList<>(chunk.getItems());
      userBatchRepository.deleteAllInBatch(users);
      System.out.println("[batch] 물리삭제 : " + users.size());
    };
  }
}
