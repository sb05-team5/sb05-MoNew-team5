package com.sprint.project.monew.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.project.monew.exception.BusinessException;
import com.sprint.project.monew.exception.ErrorCode;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.mapper.UserMapper;
import com.sprint.project.monew.user.repository.UserRepository;
import java.time.Instant;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private UUID id;
  private String email;
  private String nickname;
  private String password;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    email = "test1@email.com";
    nickname = "test1";
    password = "qwer1234!@#$";
    user = new User(email, nickname, password);
    ReflectionTestUtils.setField(user, "id", id);
    userDto = new UserDto(id, email, nickname, user.getCreatedAt());
  }

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() {
    UserRegisterRequest request = new UserRegisterRequest(email, nickname, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(false);
    given(userRepository.existsByNickname(eq(nickname))).willReturn(false);
    given(userMapper.toUser(any(UserRegisterRequest.class))).willReturn(user);
    given(userMapper.toUserDto(any(User.class))).willReturn(userDto);

    UserDto result = userService.create(request);

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("회원 가입 시 이메일 중복이면 예외 발생")
  void create_DuplicateEmail_ThrowsException() {
    UserRegisterRequest request = new UserRegisterRequest("exist@email.com", "이메일중복", "qwer1234!@#$");
    given(userRepository.existsByEmail("exist@email.com")).willReturn(true);

    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(BusinessException.class)
        .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
            .isEqualTo(ErrorCode.DUPLICATE_EMAIL));

  }

  @Test
  @DisplayName("회원 가입 시 닉네임 중복이면 예외 발생")
  void create_DuplicateNickname_ThrowsException() {
    UserRegisterRequest request = new UserRegisterRequest("new@email.com", "중복닉", "qwer1234!");
    given(userRepository.existsByEmail("new@email.com")).willReturn(false);
    given(userRepository.existsByNickname("중복닉")).willReturn(true);

    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(BusinessException.class)
        .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
            .isEqualTo(ErrorCode.DUPLICATE_NICKNAME));
  }

  @Test
  @DisplayName("회원 닉네임 변경 성공")
  void updateUser_Success() {
    UUID id = user.getId();
    UserUpdateRequest updateRequest = new UserUpdateRequest("새닉네임");
    given(userRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(user));
    given(userMapper.toUserDto(user)).willReturn(
        new UserDto(id, user.getEmail(), "새닉네임", user.getCreatedAt())
    );

    UserDto result = userService.update(id, updateRequest);

    assertThat(result.nickname()).isEqualTo("새닉네임");
    verify(userRepository).findByIdAndDeletedAtIsNull(id);
  }

  @Test
  @DisplayName("회원 삭제 시 deletedAt이 설정된다")
  void deleteSoft_Success() {
    UUID id = user.getId();
    given(userRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(user));

    userService.deleteSoft(id);

    verify(userRepository).deleteByIdForSoft(eq(id), any(Instant.class));
  }
}