package com.sprint.project.monew.service;

import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.interest.dto.InterestDto;
import com.sprint.project.monew.interest.dto.InterestQuery;
import com.sprint.project.monew.interest.dto.InterestRegisterRequest;
import com.sprint.project.monew.interest.dto.InterestUpdateRequest;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.mapper.InterestMapper;
import com.sprint.project.monew.interest.repository.InterestRepository;
import com.sprint.project.monew.interest.service.InterestService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @Mock
  private InterestMapper interestMapper;

  @InjectMocks
  private InterestService interestService;

  private Interest interest;
  private InterestDto interestDto;

  @BeforeEach
  void setUp() {
    interest = new Interest("여행", "국내,해외");
    ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());

    interestDto = new InterestDto(
        interest.getId(),
        "여행",
        List.of("국내", "해외"),
        0L,
        false
    );
  }


  @Test
  @DisplayName("관심사 생성 성공")
  void createInterest_Success() {
    // given
    when(interestRepository.findAll()).thenReturn(List.of());
    when(interestMapper.toEntity(any(InterestRegisterRequest.class))).thenReturn(interest);
    when(interestRepository.save(any(Interest.class))).thenReturn(interest);
    when(interestMapper.toDto(any(Interest.class), any(Boolean.class))).thenReturn(interestDto);

    InterestRegisterRequest request = new InterestRegisterRequest("여행", List.of("국내", "해외"));

    // when
    InterestDto result = interestService.create(request);

    // then
    assertNotNull(result);
    assertEquals("여행", result.name());
    assertEquals(List.of("국내", "해외"), result.keywords());
    assertEquals(0L, result.subscriberCount());
    assertFalse(result.subscribedByMe());

    verify(interestRepository, times(1)).save(any(Interest.class));
  }

//  @Test
//  @DisplayName("관심사 생성 실패 - 유사한 이름의 관심사가 이미 존재할 때 예외 발생")
//  void createInterest_throwsException_whenSimilarNameExists() {
//    // given
//    Interest existing = new Interest("여행하기1", "자연,해외");
//    when(interestRepository.findAll()).thenReturn(List.of(existing));
//
//    InterestRegisterRequest request = new InterestRegisterRequest("여행하기2", List.of("자연", "국내"));
//
//    // when & then
//    IllegalArgumentException exception = assertThrows(
//        IllegalArgumentException.class,
//        () -> interestService.create(request)
//    );
//
//    assertEquals("유사한 이름의 관심사가 이미 존재합니다.", exception.getMessage());
//  }
//
//  @Test
//  @DisplayName("관심사 목록 조회 성공 - 이름 오름차순")
//  void findAllInterests_Success_sortedByName() {
//    // given
//    InterestQuery interestQuery = new InterestQuery("여행", null, null, null, 10, "name", "asc");
//
//    List<InterestDto> interests = List.of(
//        new InterestDto(UUID.randomUUID(), "가방", List.of("패션"), 0L, false),
//        new InterestDto(UUID.randomUUID(), "자전거", List.of("운동"), 0L, false)
//    );
//
//    CursorPageResponse<InterestDto> response = new CursorPageResponse<>(interests, null, null, 10, false, interests.size());
//
//    when(interestRepository.findAll(interestQuery)).thenReturn(response);
//
//    // when
//    CursorPageResponse<InterestDto> result = interestService.findAll(interestQuery);
//
//    // then
//    assertNotNull(result);
//    assertEquals("가방", result.content().get(0).name());
//    assertEquals("자전거", result.content().get(1).name());
//    verify(interestRepository, times(1)).findAll(interestQuery);
//  }


  @Test
  @DisplayName("관심사 수정 성공")
  void updateInterest_Success() {
    // given
    when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));

    InterestUpdateRequest request = new InterestUpdateRequest(List.of("바다", "산"));

    InterestDto updated = new InterestDto(
        interest.getId(),
        "여행",
        List.of("바다", "산"),
        0L,
        false
    );
    when(interestMapper.toDto(any(Interest.class), eq(true))).thenReturn(updated);

    // when
    InterestDto result = interestService.update(interest.getId(), request);

    // then
    assertEquals(List.of("바다", "산"), result.keywords());
    assertEquals("여행", result.name());
    assertFalse(result.subscribedByMe());

    verify(interestRepository, times(1)).findById(interest.getId());
  }

  @Test
  @DisplayName("관심사 수정 실패 - 수정할 관심사가 존재하지 않을 때 예외 발생")
  void updateInterest_throwsException_whenNotFound() {
    // given
    when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    InterestUpdateRequest request = new InterestUpdateRequest(List.of("바다", "산"));

    // when & then
    NoSuchElementException exception = assertThrows(
        NoSuchElementException.class,
        () -> interestService.update(interest.getId(), request)
    );

    assertEquals("관심사가 존재하지 않습니다.", exception.getMessage());
    verify(interestRepository, times(1)).findById(interest.getId());
    verify(interestRepository, never()).save(any());  // 업데이트 함수 없어서 save로
  }

  @Test
  @DisplayName("관심사 수정 실패 - 키워드가 중복될 때 예외 발생")
  public void updateInterest_throwsException_whenDuplicateKeywords() {
    // given
    when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));

    InterestUpdateRequest request = new InterestUpdateRequest(List.of("바다", "바다"));

    // when & then
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> interestService.update(interest.getId(), request)
    );

    assertEquals("키워드 목록에 중복된 값이 있습니다.", exception.getMessage());

    verify(interestRepository, times(1)).findById(interest.getId());
    verify(interestRepository, never()).save(any());

  }


  @Test
  @DisplayName("관심사 삭제 성공")
  void deleteInterest_Success() {
    // given
    when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));

    // when
    interestService.delete(interest.getId());

    // then
    verify(interestRepository, times(1)).findById(interest.getId());
    verify(interestRepository, times(1)).delete(interest);
  }

  @Test
  @DisplayName("관심사 삭제 실패 - 삭제할 관심사가 존재하지 않을 때 예외 발생")
  void deleteInterest_throwsException_whenNotFound() {
    // given
    when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // when & then
    NoSuchElementException exception = assertThrows(
        NoSuchElementException.class,
        () -> interestService.delete(interest.getId())
    );

    assertEquals("관심사가 존재하지 않습니다.", exception.getMessage());
    verify(interestRepository, times(1)).findById(interest.getId());
    verify(interestRepository, never()).delete(interest);
  }
}


