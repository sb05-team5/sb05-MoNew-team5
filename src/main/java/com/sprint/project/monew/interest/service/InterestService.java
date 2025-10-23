package com.sprint.project.monew.interest.service;

import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.dto.InterestUpdateRequest;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.mapper.InterestMapper;
import com.sprint.project.monew.interest.repository.InterestRepository;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;

  @Transactional
  public InterestDto create(InterestRegisterRequest req) {
    String name = req.name();
    validateDuplicateName(name);

    List<String> newKeywords = req.keywords();
    validateKeywordsNotEmpty(newKeywords);

    Interest interest = interestMapper.toEntity(req);
    interestRepository.save(interest);

    return interestMapper.toDto(interest);
  }

  @Transactional
  public InterestDto update(UUID InterestId, InterestUpdateRequest req) {
    Interest interest = validatedInterestId(InterestId);

    List<String> newKeywords = req.keywords();
    validateKeywordsNotEmpty(newKeywords);

    validateDuplicateKeywords(newKeywords);

    interest.update(newKeywords);

    return interestMapper.toDto(interest);
  }

  private void validateDuplicateName(String name) {
    if (interestRepository.existsByNameSimilarity(name)) {
      throw new IllegalArgumentException("유사한 이름이 이미 존재합니다.");
    }
  }

  private Interest validatedInterestId(UUID InterestId) {
    return interestRepository.findById(InterestId)
        .orElseThrow(() -> new NoSuchElementException("관심사가 존재하지 않습니다."));
  }

  private static void validateKeywordsNotEmpty(List<String> newKeywords) {
    if (newKeywords == null || newKeywords.isEmpty()) {
      throw new IllegalArgumentException("키워드는 비어 있을 수 없습니다.");
    }
  }

  private static void validateDuplicateKeywords(List<String> newKeywords) {
    Set<String> combined = new HashSet<>(newKeywords);
    if (combined.size() < newKeywords.size()) {
      throw new IllegalArgumentException("키워드 목록에 중복된 값이 있습니다.");
    }
  }

}
