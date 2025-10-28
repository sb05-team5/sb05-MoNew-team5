package com.sprint.project.monew.articleBackup.backupBatch.writer;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.articleBackup.backupBatch.dto.InterestKeywordDto;
import com.sprint.project.monew.articleBackup.backupBatch.processor.ArticleBackupProcessor;
import com.sprint.project.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class ArticleWriter implements ItemWriter<Article> {

    private final ArticleRepository articleRepository;


    @Override
    public void write(Chunk<? extends Article> chunk) throws Exception {
        for (Article article : chunk) {
            try {
                articleRepository.save(article);
                Thread.sleep(700);
            } catch (DataIntegrityViolationException e) {
                // 중복 발생 등 무시
            }
        }


    }
}
