package com.sprint.project.monew.articleBackup.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.articleBackup.dto.ArticleBackupDto;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleBackup.entity.QArticleBackup;
import com.sprint.project.monew.articleBackup.repository.ArticleBackupQueryRepository;
import com.sprint.project.monew.comment.entity.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleBackupQueryRepositoryImpl implements ArticleBackupQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QArticleBackup b=QArticleBackup.articleBackup;
    private final QComment c=QComment.comment;


    @Override
    public List<ArticleBackupDto> searchForRestore(String from, String to) {
        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(b.deleted_at.isNull());

        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

        if (from != null && !from.isBlank()) {
            Instant fromInstant = parseToInstant(from, zone, dateTimeFormatter);
            if (fromInstant != null) {
                builder.and(b.createdAt.goe(fromInstant));
            }
        }

        if (to != null && !to.isBlank()) {
            Instant toInstant = parseToInstant(to, zone, dateTimeFormatter);
            if (toInstant != null) {
                builder.and(b.createdAt.loe(toInstant));
            }
        }

        return queryFactory
                .selectDistinct(Projections.constructor(
                        ArticleBackupDto.class,
                        b.id,
                        b.createdAt,
                        b.article_id,
                        b.publishDate,
                        b.source,
                        b.sourceUrl,
                        b.title,
                        b.summary,
                        b.viewCount,
                        b.interest_id,
                        b.deleted_at
                ))
                .from(b)
                .where(builder)
                .fetch();

    }

    private Instant parseToInstant(String text, ZoneId zone, DateTimeFormatter formatter) {
        try {
            if (text.endsWith("Z")) {
                return Instant.parse(text);
            } else {
                LocalDateTime ldt = LocalDateTime.parse(text, formatter);
                return ldt.atZone(zone).toInstant();
            }
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
