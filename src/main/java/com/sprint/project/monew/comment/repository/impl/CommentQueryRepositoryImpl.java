package com.sprint.project.monew.comment.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.project.monew.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

//    private final JPAQueryFactory queryFactory;
//
//    // 동일 기사와 논리삭제 된 기사 제외
//    private static BooleanBuilder filter(UUID articleId) {
//        return new BooleanBuilder()
//                .and(comment.articleId.eq(articleId))
//                .and(comment.deletedAt.isNull());
//    }
//
//    @Override
//    public long countByArticleId(UUID articleId) {
//        Long count = queryFactory
//                .select(comment.count())
//                .from(comment)
//                .where(
//                        comment.articleId.eq(articleId),
//                        comment.deletedAt.isNull()
//                )
//                .fetchOne();
//
//        return count == null ? 0L : count;
//    }
//
//    @Override
//    public List<Comment> pageByDateDesc(UUID articleId, Instant createdAtCursor, UUID idCursor, int size) {
//        BooleanBuilder where = filter(articleId);
//
//        if (createdAtCursor != null && idCursor != null) {
//            where.and(
//                    comment.createdAt.lt(createdAtCursor)
//                            .or(comment.createdAt.eq(createdAtCursor).and(comment.id.lt(idCursor)))
//            );
//        }
//
//        return queryFactory
//                .selectFrom(comment)
//                .where(where)
//                .orderBy(
//                        new OrderSpecifier<>(Order.DESC, comment.createdAt),
//                        new OrderSpecifier<>(Order.DESC, comment.id)
//                )
//                .limit(size)
//                .fetch();
//    }
//
//    @Override
//    public List<Comment> pageByDateAsc(UUID articleId, Instant createdAtCursor, UUID idCursor, int size) {
//        BooleanBuilder where = filter(articleId);
//
//        if (createdAtCursor != null && idCursor != null) {
//            where.and(
//                    comment.createdAt.gt(createdAtCursor)
//                            .or(comment.createdAt.eq(createdAtCursor).and(comment.id.gt(idCursor)))
//            );
//        }
//
//        return queryFactory
//                .selectFrom(comment)
//                .where(where)
//                .orderBy(
//                        new OrderSpecifier<>(Order.ASC, comment.createdAt),
//                        new OrderSpecifier<>(Order.ASC, comment.id)
//                )
//                .limit(size)
//                .fetch();
//    }
//
//    @Override
//    public List<Comment> pageByLikesDesc(UUID articleId, int likeCountCursor, UUID idCursor, int size) {
//        BooleanBuilder where = filter(articleId);
//
//        if (idCursor != null) {
//            where.and(
//                    comment.likeCount.lt(likeCountCursor)
//                            .or(comment.likeCount.eq(likeCountCursor).and(comment.id.lt(idCursor)))
//            );
//        }
//
//        return queryFactory
//                .selectFrom(comment)
//                .where(where)
//                .orderBy(
//                        new OrderSpecifier<>(Order.DESC, comment.likeCount),
//                        new OrderSpecifier<>(Order.DESC, comment.id)
//                )
//                .limit(size)
//                .fetch();
//    }
//
//    @Override
//    public List<Comment> pageByLikesAsc(UUID articleId, int likeCountCursor, UUID idCursor, int size) {
//        BooleanBuilder where = filter(articleId);
//
//        if (idCursor != null) {
//            where.and(
//                    comment.likeCount.gt(likeCountCursor)
//                            .or(comment.likeCount.eq(likeCountCursor).and(comment.id.gt(idCursor)))
//            );
//        }
//
//        return queryFactory
//                .selectFrom(comment)
//                .where(where)
//                .orderBy(
//                        new OrderSpecifier<>(Order.ASC, comment.likeCount),
//                        new OrderSpecifier<>(Order.ASC, comment.id)
//                )
//                .limit(size)
//                .fetch();
//    }
}
