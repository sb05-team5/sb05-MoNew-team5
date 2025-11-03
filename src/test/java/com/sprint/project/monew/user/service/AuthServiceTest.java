package com.sprint.project.monew.user.service;

import com.sprint.project.monew.exception.BusinessException;
import com.sprint.project.monew.exception.ErrorCode;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private AuthService authService;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    user = new User("test@email.com", "테스트", "qwer1234!");
    userDto = new UserDto(UUID.randomUUID(), user.getEmail(), user.getNickname(), Instant.now());
  }

  @Test
  @DisplayName("로그인 성공 시 UserDto 반환")
  void login_Success() {
    UserLoginRequest request = new UserLoginRequest("test@email.com", "qwer1234!");
    given(userRepository.findByEmailAndDeletedAtIsNull("test@email.com"))
        .willReturn(Optional.of(user));
    given(userMapper.toUserDto(user)).willReturn(userDto);

    UserDto result = authService.login(request);

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findByEmailAndDeletedAtIsNull("test@email.com");
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 USER_NOT_FOUND 예외 발생")
  void login_NonExistingEmail_ThrowsException() {
    UserLoginRequest request = new UserLoginRequest("none@email.com", "qwer1234!");
    given(userRepository.findByEmailAndDeletedAtIsNull("none@email.com"))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BusinessException.class)
        .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
            .isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("비밀번호 불일치 시 INVALID_PASSWORD 예외 발생")
  void login_WrongPassword_ThrowsException() {
    UserLoginRequest request = new UserLoginRequest("test@email.com", "wrongpassword");
    given(userRepository.findByEmailAndDeletedAtIsNull("test@email.com"))
        .willReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BusinessException.class)
        .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
            .isEqualTo(ErrorCode.INVALID_PASSWORD));
  }
}