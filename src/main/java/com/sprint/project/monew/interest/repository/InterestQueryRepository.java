package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import java.time.Instant;
import java.util.UUID;


public interface InterestQueryRepository {
  CursorPageResponse<InterestDto> findAll(
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      Instant after,
      int size,
      UUID userId);
}
