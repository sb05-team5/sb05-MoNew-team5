package com.sprint.project.monew.article.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleDtoUUID;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.entity.QArticle;
import com.sprint.project.monew.article.entity.Source;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.repository.ArticleQueryRepository;
import com.sprint.project.monew.articleView.entity.QArticleView;
import com.sprint.project.monew.comment.entity.QComment;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.entity.QInterest;
import com.sprint.project.monew.user.entity.QUser;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository{
    private final JPAQueryFactory queryFactory;
    private static final QArticle a = QArticle.article;
    private static final QInterest i = QInterest.interest;
    private static final QComment c = QComment.comment;
    private static final QArticleView v = QArticleView.articleView;
    private static final QUser u = QUser.user;
    private final ArticleMapper articleMapper;


    @Override
    public ArticleDtoUUID searchOne(UUID articleId,UUID  userId) {

        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());
        builder.and(a.id.eq(articleId));

        // viewedByMe 서브쿼리: articleView에 내가 본 기록이 존재하는지 여부
        BooleanExpression viewedByMeExpr = JPAExpressions
                .selectOne()
                .from(v)
                .where(v.article.id.eq(articleId).and(v.user.id.eq(userId)).and(v.deleted_at.isNull()))
                .exists();

        builder.and(viewedByMeExpr);

        return queryFactory
                .select(Projections.constructor(
                                ArticleDtoUUID.class,
                                a.id,
                                a.createdAt,
                                a.source,
                                a.sourceUrl,
                                a.title,
                                a.publishDate,
                                a.summary,
                                a.commentCount,
                                a.viewCount,
                                a.deleted_at,
                                viewedByMeExpr
                        ))
                .from(a)
                .where(builder)
                .fetchOne();
    }

    @Override
    public Long getCommentCount(UUID articleId) {
        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(c.deletedAt.isNull());
        builder.and(c.article.id.eq(articleId));

        Long countResult = queryFactory
                .select(c.id.countDistinct())
                .from(c)
                .where(builder)
                .fetchOne();

        // null이면 0으로 대체
        return countResult == null ? 0 : countResult;
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
                        a.commentCount,       // Long commentCount
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




    //댓글인 상황만 추가하면 끝
    @Override
    public CursorPageResponse<ArticleDto> searchPageSortedArticle(String keyword, String interestId, String sourceIn,
                                                                  String publishDateFrom, String publishDateTo,
                                                                  String orderBy, String direction, String cursor,
                                                                  String after, Integer limit , UUID userId) {

        log.info("repository --> keyword={}, interestId={}, sourceIn={}, publishDateFrom={}, publishDateTo={}, orderBy={}, direction={}, cursor={}, after={}, limit={}, userId={}",
                keyword, interestId, sourceIn,
                publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);
        // 공통 Zone과 Formatter
        ZoneId zone = ZoneId.of("Asia/Seoul");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
        // viewedByMe 서브쿼리: articleView에 내가 본 기록이 존재하는지 여부
        BooleanExpression viewedByMeExpr = JPAExpressions
                .selectOne()
                .from(v)
                .where(v.article.eq(a).and(v.user.id.eq(userId)).and(v.deleted_at.isNull()))
                .exists();

        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(a.deleted_at.isNull());
        // 페이징 처리 기본 코드
        //int page = (cursor != null && cursor > 0) ? cursor : 0; // null, 음수 방지


        int page=0;
        if (cursor != null && !cursor.isBlank()) {
            if (orderBy.equals("commentCount")) {
                String[] parts = cursor.split("\\|");
                Integer cursorCount = Integer.parseInt(parts[0]);
                UUID cursorId = (parts.length > 1) ? UUID.fromString(parts[1]) : null;
                if (direction.equals("ASC")) {
                    if (cursorId != null) {
                        builder.and(
                                a.commentCount.gt(cursorCount)
                                        .or(a.commentCount.eq(cursorCount)
                                                .and(a.id.gt(cursorId)))
                        );
                    } else {
                        builder.and(a.commentCount.gt(cursorCount));
                    }
                } else { // DESC
                    if (cursorId != null) {
                        builder.and(
                                a.commentCount.lt(cursorCount)
                                        .or(a.commentCount.eq(cursorCount)
                                                .and(a.id.lt(cursorId)))
                        );
                    } else {
                        builder.and(a.commentCount.lt(cursorCount));
                    }
                }
            }else if (orderBy.equals("viewCount")) {
                // 커서 파싱: viewCount|UUID
                String[] parts = cursor.split("\\|");
                Integer cursorView = Integer.parseInt(parts[0]);
                UUID cursorId = (parts.length > 1) ? UUID.fromString(parts[1]) : null;

                if (direction.equals("ASC")) {
                    if (cursorId != null) {
                        builder.and(
                                a.viewCount.gt(cursorView)
                                        .or(a.viewCount.eq(cursorView)
                                                .and(a.id.gt(cursorId)))
                        );
                    } else {
                        builder.and(a.viewCount.gt(cursorView));
                    }
                } else { // DESC
                    if (cursorId != null) {
                        builder.and(
                                a.viewCount.lt(cursorView)
                                        .or(a.viewCount.eq(cursorView)
                                                .and(a.id.lt(cursorId)))
                        );
                    } else {
                        builder.and(a.viewCount.lt(cursorView));
                    }
                }
            } else if (orderBy.equals("publishDate")) {
                // 기존 publishDate 커서 처리 유지
                Instant cursorInstant = parseToInstant(cursor, zone, dateTimeFormatter);
                if (cursorInstant != null) {
                    if (direction.equals("ASC")) {
                        builder.and(a.createdAt.gt(cursorInstant));
                    } else {
                        builder.and(a.createdAt.lt(cursorInstant));
                    }
                }
            }
        }


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



        // ✅ 정렬 기준 (viewCount일 때만 tie-breaker 추가)
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        switch (orderBy != null ? orderBy : "createdAt") {
            case "commentCount":
                if (direction.equals("ASC")) {
                    orderSpecifiers.add(a.commentCount.asc());
                    orderSpecifiers.add(a.id.asc());   // tie-breaker
                } else {
                    orderSpecifiers.add(a.commentCount.desc());
                    orderSpecifiers.add(a.id.desc());  // tie-breaker
                }
                break;
            case "viewCount":
                if (direction.equals("ASC")) {
                    orderSpecifiers.add(a.viewCount.asc());
                    orderSpecifiers.add(a.id.asc());   // tie-breaker
                } else {
                    orderSpecifiers.add(a.viewCount.desc());
                    orderSpecifiers.add(a.id.desc());  // tie-breaker
                }
                break;
            case "publishDate":
                orderSpecifiers.add(direction.equals("ASC") ? a.createdAt.asc() : a.createdAt.desc());
                break;
            default:
                orderSpecifiers.add(direction.equals("ASC") ? a.createdAt.asc() : a.createdAt.desc());
        }



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
                        a.commentCount,
                        a.viewCount,
                        a.deleted_at,
                        viewedByMeExpr
                ))
                .from(a)
                .leftJoin(c).on(c.article.id.eq(a.id))
                .fetchJoin()
                .where(builder)
                .groupBy(
                        a.id,
                        a.createdAt
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .limit(limit + 1)
                .fetch();


        List<ArticleDto> articleDtos= articleUDtos.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
        log.info("dtos->{}, size->{}", articleDtos,articleDtos.size());
        // Slice로 감싸기
        boolean hasNext = articleDtos.size() > limit;
        List<ArticleDto> contents = hasNext ? articleDtos.subList(0, limit) : articleDtos;

// ✅ nextAfter: 다음 페이지가 있을 때만 마지막 createdAt 사용
        String nextCursor = null;
        if (hasNext && !contents.isEmpty()) {
            ArticleDto last = contents.get(contents.size() - 1);
            switch (orderBy) {
                case "commentCount":
                    nextCursor = last.commentCount() + "|" + last.id().toString();
                    break;
                case "viewCount":
                    nextCursor = last.viewCount() + "|" + last.id().toString();
                    break;
                case "publishDate":
                    nextCursor = last.createdAt().toString();
                    break;
            }
            log.info("nextCursor -> {}", nextCursor);
        }


//                List<T> content,
//                String nextCursor,
//                String nextAfter,
//                Integer size,
//                Boolean hasNext,
//                Integer totalElements
//

        return new CursorPageResponse<>(
                contents,
                nextCursor,
                after,
                limit,
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
