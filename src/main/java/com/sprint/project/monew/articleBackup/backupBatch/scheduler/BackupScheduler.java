package com.sprint.project.monew.articleBackup.backupBatch.scheduler;

import com.sprint.project.monew.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class BackupScheduler {
    private final JobLauncher jobLauncher;

    private final Job articleBackupJob;
    private final Job articleJob;
    private final RetryTemplate retryTemplate;

    @Scheduled(cron = "${spring.backup.job1.cron}")
    public void runBackupJob()
            throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        retryTemplate.execute(context -> {
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지
                        .toJobParameters();

                jobLauncher.run(articleBackupJob, jobParameters);
                System.out.println("✅ Article Backup Job executed successfully!");
            } catch (Exception e) {
                System.err.println("❌ Article Backup Job failed: " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        });
    }

    @Scheduled(cron = "${spring.backup.job2.cron}")
    public void runArticleJob()
            throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        retryTemplate.execute(context->{
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지
                        .toJobParameters();

                jobLauncher.run(articleJob, jobParameters);
                System.out.println("✅ Article Job executed successfully!");
            } catch (Exception e) {
                System.err.println("❌ Article Job failed: " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        });

    }



}
