package com.sprint.project.monew.user.service;

import com.sprint.project.monew.exception.BusinessException;
import com.sprint.project.monew.exception.ErrorCode;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto login(UserLoginRequest userLoginRequest) {
    User user = userRepository.findByEmailAndDeletedAtIsNull(userLoginRequest.email())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    if (!user.getPassword().equals(userLoginRequest.password())) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }
    return userMapper.toUserDto(user);
  }
}
