package com.sprint.project.monew.articleBackup.backupBatch.job;


import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleBackup.backupBatch.processor.ArticleBackupProcessor;
import com.sprint.project.monew.articleBackup.backupBatch.reader.ArticleBackupReader;
import com.sprint.project.monew.articleBackup.backupBatch.writer.ArticleBackupWriter;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ArticleBackupStepConfig {
    private final ArticleBackupReader articleBackupReader;
    private final ArticleBackupProcessor articleBackupProcessor;
    private final ArticleBackupWriter articleBackupWriter;



    @Bean
    public Step articleBackupStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleBackupStep" , jobRepository)
                .<Article, ArticleBackup> chunk(500,transactionManager)
                .reader(articleBackupReader)
                .processor(articleBackupProcessor)
                .writer(articleBackupWriter)
                .build();
    }




}
