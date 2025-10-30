package com.sprint.project.monew.interest.repository.impl;

import static com.sprint.project.monew.interest.entity.QInterest.interest;
import static com.sprint.project.monew.interest.entity.QSubscription.subscription;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.repository.InterestQueryRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class InterestQueryRepositoryImpl implements InterestQueryRepository {

  private final JPAQueryFactory queryFactory;

  public InterestQueryRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
    Order order = "DESC".equalsIgnoreCase(direction) ? Order.DESC : Order.ASC;

    ComparableExpressionBase<?> orderTarget;
    if ("subscriberCount".equalsIgnoreCase(orderBy)) {
      orderTarget = interest.subscriberCount;
    } else {
      orderTarget = interest.name;
    }

    return new OrderSpecifier(order, orderTarget);
  }

  private BooleanExpression keywordContains(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    String searchKeyword = "%" + keyword.toLowerCase() + "%";

    return interest.name.toLowerCase().like(searchKeyword)
        .or(interest.keyword.toLowerCase().like(searchKeyword));
  }

  private BooleanExpression cursorAndAfterFilter(String cursor, Instant after, String orderBy, String direction) {
    if (!StringUtils.hasText(cursor) || after == null) {
      return null;
    }

    ComparableExpressionBase<?> orderTarget;
    if ("subscriberCount".equalsIgnoreCase(orderBy)) {
      orderTarget = interest.subscriberCount;
    } else {
      orderTarget = interest.name;
    }

    BooleanExpression mainCondition;
    if ("ASC".equalsIgnoreCase(direction)) {
      mainCondition = Expressions.stringTemplate("STR({0})", orderTarget).gt(cursor);
      BooleanExpression tieCondition = Expressions.stringTemplate("STR({0})", orderTarget).eq(cursor)
          .and(interest.createdAt.gt(after));
      return mainCondition.or(tieCondition);
    } else {
      mainCondition = Expressions.stringTemplate("STR({0})", orderTarget).lt(cursor);
      BooleanExpression tieCondition = Expressions.stringTemplate("STR({0})", orderTarget).eq(cursor)
          .and(interest.createdAt.lt(after));
      return mainCondition.or(tieCondition);
    }
  }

  @Override
  public CursorPageResponse<InterestDto> findAll(
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      Instant after,
      int size,
      UUID userId) {

    BooleanBuilder whereCondition = new BooleanBuilder();
    whereCondition.and(keywordContains(keyword));
    whereCondition.and(cursorAndAfterFilter(cursor, after, orderBy, direction));

    OrderSpecifier<?> mainOrder = getOrderSpecifier(orderBy, direction);
    OrderSpecifier<?> secondaryOrder = "DESC".equalsIgnoreCase(direction)
        ? interest.createdAt.desc()
        : interest.createdAt.asc();

    List<Interest> results = queryFactory
        .selectFrom(interest)
        .where(whereCondition)
        .orderBy(mainOrder, secondaryOrder)
        .limit(size + 1)
        .fetch();

    boolean hasNext = results.size() > size;
    if (hasNext) {
      results.remove(size);
    }

    List<InterestDto> content = results.stream()
        .map(i -> new InterestDto(
            i.getId(),
            i.getName(),
            i.getKeywords(),
            i.getSubscriberCount(),
            isSubscribed(i.getId(), userId)
        ))
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;
    if (hasNext) {
      Interest lastInterest = results.get(results.size() - 1);

      if ("subscriberCount".equalsIgnoreCase(orderBy)) {
        nextCursor = String.valueOf(lastInterest.getSubscriberCount());
      } else {
        nextCursor = lastInterest.getName();
      }
      nextAfter = lastInterest.getCreatedAt();
    }

    return new CursorPageResponse<>(
        content,
        nextCursor,
        nextAfter != null ? nextAfter.toString() : null,
        size,
        hasNext,
        null
    );
  }

  private Boolean isSubscribed(UUID interestId, UUID userId) {
    if (userId == null) {
      return false;
    }

    Integer count = queryFactory
        .selectOne()
        .from(subscription)
        .where(subscription.interest.id.eq(interestId)
            .and(subscription.user.id.eq(userId)))
        .fetchFirst();

    return count != null;
  }
}