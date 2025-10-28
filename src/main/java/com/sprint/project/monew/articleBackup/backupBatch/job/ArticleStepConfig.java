package com.sprint.project.monew.articleBackup.backupBatch.job;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleBackup.backupBatch.dto.InterestKeywordDto;
import com.sprint.project.monew.articleBackup.backupBatch.processor.ArticleBackupProcessor;
import com.sprint.project.monew.articleBackup.backupBatch.processor.ArticleProcessor;
import com.sprint.project.monew.articleBackup.backupBatch.reader.ArticleBackupReader;
import com.sprint.project.monew.articleBackup.backupBatch.reader.ArticleReader;
import com.sprint.project.monew.articleBackup.backupBatch.writer.ArticleBackupWriter;
import com.sprint.project.monew.articleBackup.backupBatch.writer.ArticleWriter;
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
public class ArticleStepConfig {
    private final ArticleReader articleReader;
    private final ArticleProcessor articleProcessor;
    private final ArticleWriter articleWriter;


    @Bean
    public Step articleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleStep" , jobRepository)
                .<InterestKeywordDto, Article> chunk(500,transactionManager)
                .reader(articleReader)
                .processor(articleProcessor)
                .writer(articleWriter)
                .build();
    }

}
