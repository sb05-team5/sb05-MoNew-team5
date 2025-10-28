package com.sprint.project.monew.articleBackup.backupBatch.reader;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleBackupReader implements ItemReader<Article> {
    private final ArticleRepository articleRepository;
    private Iterator<Article> iterator;

    @Override
    public Article read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(iterator == null){
            iterator = articleRepository.findAll().iterator();
            log.info("backupReader --> {}", iterator);
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
