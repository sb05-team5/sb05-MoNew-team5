package com.sprint.project.monew.interest.service;

import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.mapper.InterestMapper;
import com.sprint.project.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;

  public InterestDto create(InterestRegisterRequest req) {
    if (interestRepository.existsByNameSimilarity(req.name())) {
      throw new IllegalArgumentException("유사한 이름이 이미 존재합니다.");
    }

    Interest interest = interestMapper.toEntity(req);

    interestRepository.save(interest);
    return interestMapper.toDto(interest);
  }

}
