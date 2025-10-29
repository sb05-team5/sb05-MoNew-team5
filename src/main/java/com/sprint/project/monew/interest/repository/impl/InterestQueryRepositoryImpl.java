package com.sprint.project.monew.interest.repository.impl;

import static com.sprint.project.monew.interest.entity.QInterest.interest;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.mapper.InterestMapper;
import com.sprint.project.monew.interest.repository.InterestQueryRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class InterestQueryRepositoryImpl implements InterestQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final InterestMapper interestMapper;

  @Override
  public CursorPageResponse<InterestDto> findAll(InterestQuery query) {

    BooleanExpression searchCondition = buildSearchCondition(query);
    BooleanExpression cursorCondition = buildCursorCondition(query);

    Long totalElementsLong = queryFactory
        .select(interest.count())
        .from(interest)
        .where(searchCondition)
        .fetchOne();
    int totalElements = totalElementsLong != null ? totalElementsLong.intValue() : 0;

    List<Interest> interests = queryFactory
        .selectFrom(interest)
        .where(searchCondition, cursorCondition)
        .orderBy(sortResolve(query.sortField(), query.sortDirection()))
        .limit(query.size() + 1)
        .fetch();

    boolean hasNext = interests.size() > query.size();
    if (hasNext) interests = interests.subList(0, query.size());

    String nextCursor = null;
    String nextAfter = null;
    if (!interests.isEmpty()) {
      Interest last = interests.get(interests.size() - 1);
      nextCursor = "subscriberCount".equalsIgnoreCase(query.sortField())
          ? String.valueOf(last.getSubscriberCount())
          : last.getName();
      nextAfter = last.getId().toString();
    }

    List<InterestDto> dtoList = interests.stream()
        .map(i -> interestMapper.toDto(i, false))
        .toList();

    return new CursorPageResponse<>(
        dtoList, nextCursor, nextAfter,
        query.size(), hasNext, totalElements
    );
  }

  private BooleanExpression buildSearchCondition(InterestQuery query) {
    BooleanExpression nameCond = StringUtils.hasText(query.name())
        ? interest.name.containsIgnoreCase(query.name())
        : null;
    BooleanExpression keywordCond = StringUtils.hasText(query.keyword())
        ? interest.keyword.containsIgnoreCase(query.keyword())
        : null;

    return Stream.of(nameCond, keywordCond)
        .filter(Objects::nonNull)
        .reduce(BooleanExpression::or)
        .orElse(null);
  }

  private BooleanExpression buildCursorCondition(InterestQuery query) {
    if (!StringUtils.hasText(query.cursor()) || !StringUtils.hasText(query.idAfter())) {
      return null;
    }

    boolean isDesc = "desc".equalsIgnoreCase(query.sortDirection());
    UUID afterId = UUID.fromString(query.idAfter());

    if ("subscriberCount".equalsIgnoreCase(query.sortField())) {
      long cursorValue = Long.parseLong(query.cursor());
      return isDesc
          ? interest.subscriberCount.lt(cursorValue)
          .or(interest.subscriberCount.eq(cursorValue).and(interest.id.lt(afterId)))
          : interest.subscriberCount.gt(cursorValue)
              .or(interest.subscriberCount.eq(cursorValue).and(interest.id.gt(afterId)));
    } else {
      String cursorValue = query.cursor();
      return isDesc
          ? interest.name.lt(cursorValue)
          .or(interest.name.eq(cursorValue).and(interest.id.lt(afterId)))
          : interest.name.gt(cursorValue)
              .or(interest.name.eq(cursorValue).and(interest.id.gt(afterId)));
    }
  }

  private OrderSpecifier<?>[] sortResolve(String sortField, String sortDirection) {
    Order order = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;

    if ("subscriberCount".equalsIgnoreCase(sortField)) {
      return new OrderSpecifier[]{
          new OrderSpecifier<>(order, interest.subscriberCount),
          new OrderSpecifier<>(order, interest.id)
      };
    } else {
      return new OrderSpecifier[]{
          new OrderSpecifier<>(order, interest.name),
          new OrderSpecifier<>(order, interest.id)
      };
    }
  }
}