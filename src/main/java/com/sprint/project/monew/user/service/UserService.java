package com.sprint.project.monew.user.service;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.NoSuchElementException;
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
      throw new RuntimeException("이미 존재하는 이메일입니다.");
    }
    if (userRepository.existsByNickname(userRegisterRequest.nickname())) {
      throw new RuntimeException("이미 존재하는 닉네임입니다.");
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
    User user = userRepository.findById(id)
        .orElseThrow(()-> new NoSuchElementException("존재하지 않는 아이디"));
    if(user.getNickname().equals(request.newNickname())) {
      throw new RuntimeException("현재 닉네임과 같은 닉네임");
    }
    user.changeNickname(request.newNickname());
    return userMapper.toUserDto(user);
  }

  @Transactional
  public void deleteSoft(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(()->new NoSuchElementException("존재하지 않는 아이디"));
    userRepository.deleteByIdForSoft(id, Instant.now());
  }

  @Transactional
  public void deleteById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(()->new NoSuchElementException("존재하지 않는 아이디"));
    userRepository.deleteById(id);
  }
}
