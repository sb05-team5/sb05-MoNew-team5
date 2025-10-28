package com.sprint.project.monew.articleBackup.backupBatch.processor;


import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ArticleBackupProcessor implements ItemProcessor<Article, ArticleBackup> {


    @Override
    public ArticleBackup process(Article item) throws Exception {
        log.info("articleProcessor --> {}",ArticleBackup.builder()
                .id(item.getId())// 필요시 새 ID 생성
                .build());

        return ArticleBackup.builder()
                .article_id(item.getId())
                .createdAt(item.getCreatedAt())
                .source(item.getSource())
                .sourceUrl(item.getSourceUrl())
                .title(item.getTitle())
                .publishDate(item.getPublishDate())
                .summary(item.getSummary())
                .viewCount(item.getViewCount())
                .interest_id(item.getInterest_id())
                .deleted_at(item.getDeleted_at())
                .build();
    }
}
