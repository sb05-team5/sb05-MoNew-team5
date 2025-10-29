package com.sprint.project.monew.comment.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.comment.repository.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.sprint.project.monew.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countActiveByArticleId(UUID articleId) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.articleId.eq(articleId),
                        comment.deletedAt.isNull()
                ).fetchOne();

        return count == null ? 0L : count;
    }
}
