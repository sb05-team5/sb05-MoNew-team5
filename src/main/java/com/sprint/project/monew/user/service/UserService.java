package com.sprint.project.monew.user.service;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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
      throw new RuntimeException("이미 존재하는 이메일입니다.");
    }

    if (userRepository.existsByNickname(userRegisterRequest.nickname())) {
      throw new RuntimeException("이미 존재하는 닉네임입니다.");
    }

    User newUser = userMapper.toUser(userRegisterRequest);
    System.out.println("newUser email: " + newUser.getEmail());
    System.out.println("newUser nickname: " + newUser.getNickname());
    System.out.println("newUser password: " + newUser.getPassword());

    try {
      userRepository.save(newUser);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return userMapper.toUserDto(newUser);
  }
}
