package com.sprint.project.monew.articleView.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.articleView.entity.ArticleView;
import com.sprint.project.monew.articleView.entity.QArticleView;
import com.sprint.project.monew.articleView.repository.ArticleViewQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleViewQueryRepositoryImpl implements ArticleViewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QArticleView v=QArticleView.articleView;



    @Override
    public boolean existsById(UUID articleId, UUID userId) {
        BooleanBuilder builder = new BooleanBuilder();
        // soft delete 처리
        builder.and(v.deleted_at.isNull());
        builder.and(v.article.id.eq(articleId));
        builder.and(v.user.id.eq(userId));

        UUID viewId = queryFactory.selectFrom(v)
                .select(v.id)
                .from(v)
                .where(builder)
                .fetchOne();
        // 존재하면 true
        if( viewId != null ){
            return true ;
        }
        // 존재하지 않으면 false
        return false;
    }
}
