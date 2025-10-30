package com.sprint.project.monew.user.service;

import com.sprint.project.monew.exception.BusinessException;
import com.sprint.project.monew.exception.ErrorCode;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  public UserDto create(UserRegisterRequest userRegisterRequest) {
    if (userRepository.existsByEmail(userRegisterRequest.email())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
    if (userRepository.existsByNickname(userRegisterRequest.nickname())) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }
    User newUser = userMapper.toUser(userRegisterRequest);
    try {
      userRepository.save(newUser);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return userMapper.toUserDto(newUser);
  }

  @Transactional
  public UserDto update(UUID id, UserUpdateRequest request) {
    User user = userRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    user.changeNickname(request.newNickname());
    return userMapper.toUserDto(user);
  }

  @Transactional
  public void deleteSoft(UUID id) {
    User user = userRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    userRepository.deleteByIdForSoft(id, Instant.now());
  }

  @Transactional
  public void deleteById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    userRepository.deleteById(id);
  }
}
