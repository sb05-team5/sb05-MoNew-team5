package com.sprint.project.monew.comment.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import static com.sprint.project.monew.commentLike.entity.QCommentLike.commentLike;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

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
        final boolean sortByLikes = "likeCount".equalsIgnoreCase(orderBy);
        final String dir = "asc".equalsIgnoreCase(direction) ? "asc" : "desc";
        final int size = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);

        BooleanBuilder where = new  BooleanBuilder()
                .and(comment.article.id.eq(articleId))
                .and(comment.deletedAt.isNull());

        if (after != null && !after.isBlank()) {
            String[] parts = after.split("\\|", 2);
            if (parts.length == 2) {
                try {
                    UUID cursorId = UUID.fromString(parts[1]);
                    if (sortByLikes) {
                        long likes = Long.parseLong(parts[0]);
                        applyLikeCountKeyset(where, dir, likes, cursorId);
                    } else {
                        long epochMs = Long.parseLong(parts[0]);
                        Instant createdAt = Instant.ofEpochMilli(epochMs);
                        applyCreatedAtKeyset(where, dir, createdAt, cursorId);
                    }
                } catch (Exception ignored) {}
            }
        }

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sortByLikes) {
            orders.add(new OrderSpecifier<>(toOrder(dir), likeCountExpr()));
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
            if (sortByLikes) {
                Long lastLikes = queryFactory
                        .select(likeCountExpr())
                        .from(comment)
                        .where(comment.id.eq(last.getId()))
                        .fetchOne();
                long cnt = (lastLikes != null ? lastLikes : 0L);
                nextAfter = cnt + "|" + last.getId();
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
        return queryFactory
                .select(comment.article.id)
                .from(comment)
                .where(comment.id.eq(commentId))
                .fetchOne();
    }

    private static NumberExpression<Long> likeCountExpr() {
        return Expressions.numberTemplate(
                Long.class,
                "({0})",
                JPAExpressions
                        .select(commentLike.count())
                        .from(commentLike)
                        .where(commentLike.comment.id.eq(comment.id))
        );
    }

    private static Order toOrder(String direction) {
        return ("asc".equalsIgnoreCase(direction)) ? Order.ASC : Order.DESC;
    }

    private static void applyCreatedAtKeyset(BooleanBuilder where, String dir, Instant createdAt, UUID id) {
        if (createdAt == null || id == null) return;
        if ("asc".equalsIgnoreCase(dir)) {
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

    private static void applyLikeCountKeyset(BooleanBuilder where, String dir, Long likeCount, UUID id) {
        if (likeCount == null || id == null) return;
        NumberExpression<Long> likes = likeCountExpr();
        if ("asc".equalsIgnoreCase(dir)) {
            where.and(likes.gt(likeCount)
                    .or(likes.eq(likeCount).and(comment.id.gt(id))));
        } else {
            where.and(likes.lt(likeCount)
                    .or(likes.eq(likeCount).and(comment.id.lt(id))));
        }
    }
}
