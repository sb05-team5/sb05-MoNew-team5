package com.sprint.project.monew.article.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleDtoUUID;
import com.sprint.project.monew.article.dto.ArticleRestoreResultDto;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.entity.QArticle;
import com.sprint.project.monew.article.entity.Source;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.repository.ArticleQueryRepository;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleView.entity.QArticleView;
import com.sprint.project.monew.comment.entity.QComment;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.entity.QInterest;
import com.sprint.project.monew.user.entity.QUser;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository{
    private final JPAQueryFactory queryFactory;
    private static final QArticle a =QArticle.article;
    private static final QInterest i = QInterest.interest;
    private static final QComment c = QComment.comment;
    private static final QArticleView v = QArticleView.articleView;
    private static final QUser u = QUser.user;
    private final ArticleMapper articleMapper;


    @Override
    public ArticleDto searchOne(UUID articleId,UUID  userId) {

        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());
        builder.and(a.id.eq(articleId));

        // viewedByMe 서브쿼리: articleView에 내가 본 기록이 존재하는지 여부
        BooleanExpression viewedByMeExpr = JPAExpressions
                .selectOne()
                .from(v)
                .where(v.article.eq(a).and(v.user.id.eq(userId)).and(v.deleted_at.isNull()))
                .exists();

        builder.and(viewedByMeExpr);

        return queryFactory
                .select(Projections.constructor(
                                ArticleDto.class,
                                a.id,
                                a.source,
                                a.sourceUrl,
                                a.title,
                                a.publishDate,
                                a.summary,
                                c.id.countDistinct(),
                                a.viewCount,
                                viewedByMeExpr
                        ))
                .from(a)
                .leftJoin(c).on(c.articleId.eq(a.id))
                .where(builder)
                .fetchOne();
    }


    @Override
    public List<String> searchSource() {

        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());

        List<String> sources = queryFactory
                .selectDistinct(
                        a.source
                )
                .from(a)
                .where(builder)
                .fetch();

        return sources;
    }



    @Override
    public List<ArticleDto> searchForRestore(String from, String to) {
        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());

        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

        if (from != null && !from.isBlank()) {
            Instant fromInstant = parseToInstant(from, zone, dateTimeFormatter);
            if (fromInstant != null) {
                builder.and(a.createdAt.goe(fromInstant));
            }
        }

        if (to != null && !to.isBlank()) {
            Instant toInstant = parseToInstant(to, zone, dateTimeFormatter);
            if (toInstant != null) {
                builder.and(a.createdAt.loe(toInstant));
            }
        }

        List<ArticleDtoUUID> articleUDtos= queryFactory
                .selectDistinct(Projections.constructor(
                        ArticleDtoUUID.class,
                        a.id,                           // UUID
                        a.createdAt,                    // Instant
                        a.source,                       // String
                        a.sourceUrl,                    // String
                        a.title,                        // String
                        a.publishDate,                  // String
                        a.summary,                      // String
                        Expressions.constant(0L),       // Long commentCount
                        a.viewCount,                    // Integer
                        a.deleted_at,                   // Instant
                        Expressions.constant(false)     // Boolean viewedByBme
                ))
                .from(a)
                .where(builder)
                .fetch();

        return articleUDtos.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());

    }



    @Override
    public Optional<Article> findByArticleId(UUID articleId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(a.deleted_at.isNull());
        builder.and(a.id.eq(articleId));
        return Optional.ofNullable(queryFactory
                .select(a)
                .from(a)
                .where(builder)
                .fetchOne());
    }


    @Override
    public List<Article> findAllArticle() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(a.deleted_at.isNull());
        return queryFactory
                .select(a)
                .from(a)
                .where(builder)
                .fetch();

    }

    @Override
    public List<Article> findTodayArticle(Instant day) {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(a.deleted_at.isNull());
        // 1️⃣ Instant -> ZonedDateTime (Asia/Seoul)
        ZonedDateTime zonedDay = day.atZone(zone);

        // 2️⃣ 당일 0시와 23:59:59.999 계산
        ZonedDateTime startOfDay = zonedDay.toLocalDate().atStartOfDay(zone);
        ZonedDateTime endOfDay = zonedDay.toLocalDate().atTime(LocalTime.MAX).atZone(zone);

        Instant startInstant = startOfDay.toInstant();
        Instant endInstant = endOfDay.toInstant();

        // 3️⃣ QueryDSL 조건 추가
        builder.and(a.deleted_at.isNull());
        builder.and(a.createdAt.goe(startInstant));
        builder.and(a.createdAt.loe(endInstant));

        log.info("todayArticle --> {}", queryFactory
                .select(a)
                .from(a)
                .where(builder)
                .fetch());

        return queryFactory
                .select(a)
                .from(a)
                .where(builder)
                .fetch();

    }


    @Override
    public CursorPageResponse<ArticleDto> searchPageSortedArticle(String keyword, String interestId, String sourceIn,
                                                                  String publishDateFrom, String publishDateTo,
                                                                  String orderBy, String direction, String cursor,
                                                                  String after, Integer limit , UUID userId) {

        if( orderBy.equals("publishDate") ){
            orderBy = "createdAt";
        }

        log.info("repositosy --> keyword={}, interestId={}, sourceIn={}, publishDateFrom={}, publishDateTo={}, orderBy={}, direction={}, cursor={}, after={}, limit={}, userId={}",
                keyword, interestId, sourceIn,
                publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);
        // 페이징 처리 기본 코드
        //int page = (cursor != null && cursor > 0) ? cursor : 0; // null, 음수 방지
        int page=0;
        if (cursor != null && !cursor.isBlank()) {
            try {
                page = Math.max(Integer.parseInt(cursor), 0); // 음수 방지
            } catch (NumberFormatException e) {
                // 잘못된 cursor 형식일 경우 기본값 유지
                page = 0;
            }
        }

        int size = (limit != null && limit > 0) ? limit : 10;


        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());

        //조건 시작 정리해야 됨.
        // ✅ 기본 검색 조건
        if (keyword != null && !keyword.isBlank()) {
            builder.and(a.title.containsIgnoreCase(keyword)
                    .or(a.summary.containsIgnoreCase(keyword)));
        }

        // ✅ sourceIn 필터 (예: NAVER,CHOSUN,YEONHAP,)
        if (sourceIn != null && !sourceIn.isBlank()) {
            // 문자열 배열 → trim → 필터 → Enum → 내부 String 값 추출
            List<String> sources = Arrays.stream(sourceIn.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> Source.valueOf(s).getSource()) // Enum → 내부 String 값
                    .collect(Collectors.toList());

            if (!sources.isEmpty()) {
                builder.and(a.source.in(sources));
            }
        }

        // 공통 Zone과 Formatter
        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

// ✅ 날짜 범위 필터
        if (publishDateFrom != null && !publishDateFrom.isBlank()) {
            Instant fromInstant = parseToInstant(publishDateFrom, zone, dateTimeFormatter);
            if (fromInstant != null) {
                builder.and(a.createdAt.goe(fromInstant));
            }
        }

        if (publishDateTo != null && !publishDateTo.isBlank()) {
            Instant toInstant = parseToInstant(publishDateTo, zone, dateTimeFormatter);
            if (toInstant != null) {
                builder.and(a.createdAt.loe(toInstant));
            }
        }

        // after 커서 처리 (tie-breaker 포함)
        Instant afterCreatedAt = null;
        UUID afterId = null;
        if (after != null && !after.isBlank()) {
            String[] parts = after.split("\\|");
            if (parts.length == 2) {
                afterCreatedAt = Instant.parse(parts[0]);
                afterId = UUID.fromString(parts[1]);

                // 새 BooleanBuilder에 커서 조건만 담기
                BooleanBuilder afterBuilder = new BooleanBuilder();

                if (direction.equals("ASC")) {
                    afterBuilder.and(
                            a.createdAt.gt(afterCreatedAt)
                                    .or(a.createdAt.eq(afterCreatedAt).and(a.id.gt(afterId)))
                    );
                } else {
                    afterBuilder.and(
                            a.createdAt.lt(afterCreatedAt)
                                    .or(a.createdAt.eq(afterCreatedAt).and(a.id.lt(afterId)))
                    );
                }

                // 기존 builder와 AND로 합침
                builder.and(afterBuilder);
            }
        }

//// ✅ 커서 처리 (after → 다음 페이지 기준점)
//        if (after != null && !after.isBlank()) {
//            Instant cursorInstant = parseToInstant(after, zone, dateTimeFormatter);
//            if (cursorInstant != null) {
//                if (direction.equals("ASC")) {
//                    builder.and(a.createdAt.goe(cursorInstant));
//                } else {
//                    builder.and(a.createdAt.loe(cursorInstant));
//                }
//            } else {
//                log.warn("Invalid 'after' parameter: {}", after);
//            }
//        }



        // ✅ 정렬 기준  -일단은 해보자
        OrderSpecifier<?> orderSpecifier;
        OrderSpecifier<?> orderSpecifierId;
        switch (orderBy != null ? orderBy : "createdAt") {
            case "viewCount":
                orderSpecifier = direction.equals("ASC") ? a.viewCount.asc() : a.viewCount.desc();
                break;
            case "title":
                orderSpecifier = direction.equals("ASC") ? a.title.asc() : a.title.desc();
                break;
            case "source":
                orderSpecifier = direction.equals("ASC") ? a.source.asc() : a.source.desc();
                break;
            default:
                orderSpecifier = direction.equals("ASC") ? a.createdAt.asc() : a.createdAt.desc();
        }
        if(direction.equals("ASC")){
            orderSpecifierId=a.id.asc();
        }else{
            orderSpecifierId=a.id.desc();
        }




        // viewedByMe 서브쿼리: articleView에 내가 본 기록이 존재하는지 여부
        BooleanExpression viewedByMeExpr = JPAExpressions
                .selectOne()
                .from(v)
                .where(v.article.eq(a).and(v.user.id.eq(userId)).and(v.deleted_at.isNull()))
                .exists();


        // ✅ 실제 데이터 조회 (limit + 1)
        List<ArticleDtoUUID> articleUDtos = queryFactory
                .selectDistinct(Projections.constructor(
                        ArticleDtoUUID.class,
                        a.id,
                        a.createdAt,
                        a.source,
                        a.sourceUrl,
                        a.title,
                        a.publishDate,
                        a.summary,
                        c.id.countDistinct(),
                        a.viewCount,
                        a.deleted_at,
                        viewedByMeExpr
                ))
                .from(a)
                .leftJoin(c).on(c.articleId.eq(a.id))
                .where(builder)
                .groupBy(
                        a.id,
                        a.createdAt,
                        a.source,
                        a.sourceUrl,
                        a.title,
                        a.publishDate,
                        a.summary,
                        a.viewCount,
                        a.deleted_at
                        // H2에서는 select에 있는 컬럼은 모두 group by에 넣어야 함
                )
                .orderBy(orderSpecifier,orderSpecifierId)
                .limit(size + 1)
                .fetch();

        List<ArticleDto> articleDtos= articleUDtos.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
        log.info("dtos->{}", articleDtos);
        // Slice로 감싸기
        boolean hasNext = articleDtos.size() > size;
        List<ArticleDto> contents = hasNext ? articleDtos.subList(0, size) : articleDtos;

        String nextAfter = null;
        if (!contents.isEmpty()) {
            ArticleDto last = articleDtos.get(articleDtos.size() - 1);
            nextAfter = last.createdAt().toString() + "|" + last.id(); // "|"로 구분
//            Instant lastPublishDate = contents.get(contents.size() - 1).createdAt();
//            nextAfter = lastPublishDate.toString();
        }

        return new CursorPageResponse<>(
                contents,
                null, // nextCursor는 null
                nextAfter,
                size,
                hasNext,
                contents.size()
        );
    }




    // -----------------
// 공통 유틸 메서드
    private Instant parseToInstant(String text, ZoneId zone, DateTimeFormatter formatter) {
        if (text == null || text.isBlank()) return null;
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
