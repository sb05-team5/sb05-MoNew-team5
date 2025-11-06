package com.sprint.project.monew.articleBackup.backupBatch.writer;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.articleBackup.backupBatch.dto.InterestKeywordDto;
import com.sprint.project.monew.articleBackup.backupBatch.processor.ArticleBackupProcessor;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.repository.InterestRepository;
import com.sprint.project.monew.interest.repository.SubscriptionRepository;
import com.sprint.project.monew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleWriter implements ItemWriter<Article> {

    private final ArticleRepository articleRepository;
    private final NotificationService notificationService;
    private final SubscriptionRepository subscriptionRepository;
    private  final InterestRepository interestRepository;



    @Override
    public void write(Chunk<? extends Article> chunk) throws Exception {

        for (Article article : chunk) {
            try {
                articleRepository.save(article);


                //관심사 알림 추가
                List<UUID> subscribers =subscriptionRepository.findSubscriberUserIdsByInterestId(article.getInterest_id());
                if (subscribers.isEmpty()) { continue;}

                notificationService.notifyInterestArticlesRegistered(article.getInterest_id(),
                        resolveInterestName(article.getInterest_id()),1, subscribers);;



                Thread.sleep(700);
            } catch (DataIntegrityViolationException e) {
                // 중복 발생 등 무시
                continue;
            } catch (Exception e) {
                log.error("기타 예외 발생: {}", article.getSourceUrl(), e);
            }
        }
    }


    private String resolveInterestName(UUID interestId) {
        return interestRepository.findById(interestId)
                .map(Interest::getName)
                .orElse("관심사");
    }
}
