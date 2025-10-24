package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;

public interface InterestQueryRepository {
  CursorPageResponse<InterestDto> findAll(InterestQuery interestQuery);

}
