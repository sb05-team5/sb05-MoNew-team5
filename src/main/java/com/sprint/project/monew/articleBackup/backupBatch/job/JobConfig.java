package com.sprint.project.monew.articleBackup.backupBatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final Step articleBackupStep;
    private final Step articleStep;

    @Bean
    public Job articleBackupJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("articleBackupJob" , jobRepository)
                .start(articleBackupStep)
                .build();
    }

    @Bean
    public Job articleJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("articleJob" , jobRepository)
                .start(articleStep)
                .build();
    }

}
