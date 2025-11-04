package com.sprint.project.monew.user.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class UserCleanUpScheduler {

  private final JobLauncher jobLauncher;
  private final Job userCleanUpJob;

  @Scheduled(cron = "0 0 0 * * *")
  public void runUserCleanUpJob() {

    try {
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("run.id", System.currentTimeMillis())
          .toJobParameters();

      log.info("[Scheduler] userCleanUp 시작");
      jobLauncher.run(userCleanUpJob, jobParameters);
      log.info("[Scheduler] userCleanUp 완료");
    } catch (Exception e) {
      log.error("[Scheduler] userCleanUp 실패", e);
    }
  }
}
