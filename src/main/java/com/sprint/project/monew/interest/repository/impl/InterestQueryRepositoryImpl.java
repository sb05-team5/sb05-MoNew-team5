package com.sprint.project.monew.interest.repository.impl;

import static com.sprint.project.monew.interest.entity.QInterest.interest;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.mapper.InterestMapper;
import com.sprint.project.monew.interest.repository.InterestQueryRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestQueryRepositoryImpl implements InterestQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final InterestMapper interestMapper;

  @Override
  public CursorPageResponse<InterestDto> findAll(InterestQuery query) {
    Long totalElementsLong = queryFactory
        .select(interest.count())
        .from(interest)
        .where(
            query.name() != null ? interest.name.containsIgnoreCase(query.name()) : null,
            query.keyword() != null ? interest.keyword.containsIgnoreCase(query.keyword()) : null
        )
        .fetchOne();

    Integer totalElements = totalElementsLong != null ? totalElementsLong.intValue() : 0;

    List<Interest> interests = queryFactory.selectFrom(interest)
        .where(
            query.name() != null ? interest.name.containsIgnoreCase(query.name()) : null,
            query.keyword() != null ? interest.keyword.containsIgnoreCase(query.keyword()) : null,
            buildCursorCondition(query)
        )
        .orderBy(sortResolve(query.sortField(), query.sortDirection()))
        .limit(query.size() + 1)
        .fetch();

    boolean hasNext = interests.size() > query.size();
    if (hasNext) {
      interests = interests.subList(0, query.size());
    }

    String nextCursor = null;
    String nextAfter = null;

    if (!interests.isEmpty()) {
      Interest last =  interests.get(interests.size() - 1);
      nextCursor = "name".equalsIgnoreCase(query.sortField())
          ? last.getName() : String.valueOf(last.getSubscriberCount());
      nextAfter = last.getId().toString();
    }

    List<InterestDto> interestDtos = interests.stream()
        .map(interestMapper::toDto)
        .toList();

    return new CursorPageResponse<>(
        interestDtos,
        nextCursor,
        nextAfter,
        query.size(),
        hasNext,
        totalElements

    );
  }

  private BooleanExpression buildCursorCondition(InterestQuery query) {
    if (query.cursor() == null || query.idAfter() == null) {
      return null;
    }

    boolean isDesc = "desc".equalsIgnoreCase(query.sortDirection());

    if ("name".equalsIgnoreCase(query.sortField())) {
      return isDesc ? interest.name.lt(query.cursor())
          .or(interest.name.eq(query.cursor()).and(interest.id.lt(UUID.fromString(query.idAfter()))))
          : interest.name.gt(query.cursor())
              .or(interest.name.eq(query.cursor()).and(interest.id.gt(UUID.fromString(query.idAfter()))));
    } else if ("subscriberCount".equalsIgnoreCase(query.sortField())) {
      Long cursorValue = Long.valueOf(query.cursor());
      return isDesc
          ? interest.subscriberCount.lt(cursorValue)
          .or(interest.subscriberCount.eq(cursorValue)
              .and(interest.id.lt(UUID.fromString(query.idAfter()))))
          : interest.subscriberCount.gt(cursorValue)
          .or(interest.subscriberCount.eq(cursorValue)
              .and(interest.id.gt(UUID.fromString(query.idAfter()))));
    }
    return null;
  }

  private OrderSpecifier<?>[] sortResolve(String sortField, String sortDirection) {
    Order order = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;

    if ("name".equalsIgnoreCase(sortField)) {
      return new OrderSpecifier[]{
          new OrderSpecifier<>(order, interest.name),
          new OrderSpecifier<>(order, interest.id)
      };
    } else if ("subscriberCount".equalsIgnoreCase(sortField)) {
      return new OrderSpecifier[]{
          new OrderSpecifier<>(order, interest.subscriberCount),
          new OrderSpecifier<>(order, interest.id)
      };
    }
    return new OrderSpecifier[]{
        new OrderSpecifier<>(order, interest.name),
        new OrderSpecifier<>(order, interest.id)
    };
  }
}
