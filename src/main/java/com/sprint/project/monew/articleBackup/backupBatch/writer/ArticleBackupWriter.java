package com.sprint.project.monew.articleBackup.backupBatch.writer;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleBackup.repository.ArticleBackupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleBackupWriter implements ItemWriter<ArticleBackup> {

    private final ArticleBackupRepository articleBackupRepository;

    @Override
    public void write(Chunk<? extends ArticleBackup> chunk) throws Exception {
        log.info("backupWriter --> {}", chunk.size());
        Set<ArticleBackup> uniqueArticles = new HashSet<>(chunk.getItems());
        for(ArticleBackup articleBackup : uniqueArticles){
            try {
                articleBackupRepository.save(articleBackup);
            }catch (DataIntegrityViolationException e) {
                // 중복 발생 등 무시
                continue;
            } catch (Exception e) {
                log.error("기타 예외 발생: {}", articleBackup.getSourceUrl(), e);
            }
        }

    }
}
