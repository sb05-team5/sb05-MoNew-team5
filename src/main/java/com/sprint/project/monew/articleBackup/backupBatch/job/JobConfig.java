package com.sprint.project.monew.articleBackup.backupBatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

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


    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        // 재시도할 예외 지정
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(org.springframework.dao.CannotAcquireLockException.class, true);
        retryableExceptions.put(org.postgresql.util.PSQLException.class, true);

        SimpleRetryPolicy policy = new SimpleRetryPolicy(3, retryableExceptions); // 최대 3회 재시도
        template.setRetryPolicy(policy);

        // 재시도 사이의 대기 시간 설정 (exponential backoff)
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(2000); // 2초
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000); // 최대 10초
        template.setBackOffPolicy(backOff);

        return template;
    }
}
