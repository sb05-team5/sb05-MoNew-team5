package com.sprint.project.monew.user.service;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto login(UserLoginRequest userLoginRequest) {
    User user = userRepository.findByEmail(userLoginRequest.email())
        .orElseThrow(() -> new NoSuchElementException(userLoginRequest.email() + "이메일 존재하지 않습니다."));

    if (!user.getPassword().equals(userLoginRequest.password())) {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }
    return userMapper.toUserDto(user);
  }
}
