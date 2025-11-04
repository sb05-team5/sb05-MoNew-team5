package com.sprint.project.monew.comment.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleDtoUUID;
import com.sprint.project.monew.article.entity.QArticle;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentQueryRepository;
import com.sprint.project.monew.common.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sprint.project.monew.comment.entity.QComment.comment;
@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QArticle a=QArticle.article;

    @Override
    public int countByArticleId(UUID articleId) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.article.id.eq(articleId),
                        comment.deletedAt.isNull()
                )
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public CursorPageResponse<Comment> pageByArticleSorted(UUID articleId,
                                                           String orderBy,
                                                           String direction,
                                                           String after,
                                                           Integer limit) {

        String sortKey = (orderBy == null || orderBy.isBlank()) ? "date" : orderBy.toLowerCase();
        String dir     = (direction == null || direction.isBlank()) ? "desc" : direction.toLowerCase();
        int size       = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);

        BooleanBuilder where = new  BooleanBuilder()
                .and(comment.article.id.eq(articleId))
                .and(comment.deletedAt.isNull());

        if (after != null && !after.isBlank()) {
            String[] parts = after.split("\\|", 2);
            if (parts.length == 2) {
                try {
                    UUID cursorId = UUID.fromString(parts[1]);
                    if("likes".equals(sortKey)) {
                        int likes = Integer.parseInt(parts[0]);
                        applyLikesKeyset(where, dir, likes, cursorId);
                    } else {
                        long epochMs = Long.parseLong(parts[0]);
                        Instant createdAt = Instant.ofEpochMilli(epochMs);
                        applyDateKeySet(where, dir, createdAt, cursorId);
                    }
                } catch (Exception ignored) {

                }
            }
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if("likes".equals(sortKey)) {
            orders.add(new OrderSpecifier<>(toOrder(dir), comment.likeCount));
        } else {
            orders.add(new OrderSpecifier<>(toOrder(dir), comment.createdAt));
        }

        orders.add(new OrderSpecifier<>(toOrder(dir), comment.id));

        List<Comment> rows = queryFactory
                .selectFrom(comment)
                .where(where)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .limit(size + 1L)
                .fetch();

        boolean hasNext = rows.size() > size;

        List<Comment> contents = hasNext ? rows.subList(0, size) : rows;

        String nextAfter = null;
        if (!contents.isEmpty()) {
            Comment last = contents.get(contents.size() - 1);
            if ("likes".equals(sortKey)) {
                nextAfter = last.getLikeCount() + "|" + last.getId();
            } else {
                nextAfter = last.getCreatedAt().toEpochMilli() + "|" + last.getId();
            }
        }

        int totalCount = countByArticleId(articleId);

        return new CursorPageResponse<>(
                contents,
                null,
                nextAfter,
                contents.size(),
                hasNext,
                totalCount
        );
    }

    @Override
    public UUID findArticleId(UUID commentId) {
        BooleanBuilder where = new  BooleanBuilder();
        where.and(comment.deletedAt.isNull());
        where.and(comment.id.eq(commentId));

        UUID dto = queryFactory
                .select(comment.article.id)
                .from(comment)
                .where(where)
                .fetchOne();
        return dto;
    }

    private static Order toOrder(String direction) {
        return ("asc".equalsIgnoreCase(direction)) ? Order.ASC : Order.DESC;
    }

    private static void applyDateKeySet(BooleanBuilder where, String dir, Instant createdAt, UUID id) {
        if (createdAt == null || id == null) return;

        if("asc".equalsIgnoreCase(dir)) {
            where.and(
                    comment.createdAt.gt(createdAt)
                            .or(comment.createdAt.eq(createdAt).and(comment.id.gt(id)))
            );
        } else {
            where.and(
                    comment.createdAt.lt(createdAt)
                            .or(comment.createdAt.eq(createdAt).and(comment.id.lt(id)))
            );
        }
    }

    private static void applyLikesKeyset(BooleanBuilder where, String dir, Integer likeCount, UUID id) {
        if (likeCount == null || id == null) return;
        if ("asc".equalsIgnoreCase(dir)) {
            where.and(
                    comment.likeCount.gt(likeCount)
                            .or(comment.likeCount.eq(likeCount).and(comment.id.gt(id)))
            );
        } else {
            where.and(
                    comment.likeCount.lt(likeCount)
                            .or(comment.likeCount.eq(likeCount).and(comment.id.lt(id)))
            );
        }
    }

}
