package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface InterestQueryRepository {
  CursorPageResponse<InterestDto> findAll(InterestQuery query);
}
