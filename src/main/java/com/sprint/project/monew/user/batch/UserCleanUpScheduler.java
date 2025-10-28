package com.sprint.project.monew.user.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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

      System.out.println("[Scheduler] userCleanUp 시작");
      jobLauncher.run(userCleanUpJob, jobParameters);
      System.out.println("[Scheduler] userCleanUp 완료");
    } catch (Exception e) {
      System.out.println("[Scheduler] userCleanUp 실패" + e);
    }
  }
}
