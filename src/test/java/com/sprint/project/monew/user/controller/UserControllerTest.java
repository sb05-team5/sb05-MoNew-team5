package com.sprint.project.monew.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.exception.BusinessException;
import com.sprint.project.monew.exception.ErrorCode;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.service.AuthService;
import com.sprint.project.monew.user.service.UserService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private AuthService authService;

  private UUID userId;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userDto = new UserDto(userId, "test@email.com", "테스트유저", Instant.now());
  }

  @Test
  @DisplayName("POST /api/users - 회원가입 성공 시 201 반환")
  void createUser_Success() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest("test@email.com", "테스트유저", "qwer1234!");
    given(userService.create(any(UserRegisterRequest.class))).willReturn(userDto);

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@email.com"))
        .andExpect(jsonPath("$.nickname").value("테스트유저"));
  }

  @Test
  @DisplayName("회원가입 실패 테스트 - 유효하지 않은 요청")
  void createUser_Failure_InvalidRequest() throws Exception {
    UserRegisterRequest invalidRequest = new UserRegisterRequest(
        "invalid-email",
        "aaaaaaaaaaaaaaaaaaaaa",
        "invalid-password"
    );

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("회원가입 실패 테스트 - 중복 이메일")
  void createUser_Failure_DuplicateEmail() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "test@email.com",
        "nick",
        "qwer1234!"
    );

    doThrow(new BusinessException(ErrorCode.DUPLICATE_EMAIL))
        .when(userService).create(any(UserRegisterRequest.class));

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("POST /api/users/login - 로그인 성공 시 200 및 헤더 포함")
  void login_Success() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("test@email.com", "qwer1234!");
    given(authService.login(any(UserLoginRequest.class))).willReturn(userDto);

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(header().exists("MoNew-Request-User-ID"))
        .andExpect(jsonPath("$.email").value("test@email.com"))
        .andExpect(jsonPath("$.nickname").value("테스트유저"));
  }


  @Test
  @DisplayName("PATCH /api/users/{id} - 닉네임 수정 성공 시 200 반환")
  void updateUser_Success() throws Exception {
    // given
    UserUpdateRequest request = new UserUpdateRequest("새닉네임");
    UserDto updatedUser = new UserDto(userId, "test@email.com", "새닉네임", Instant.now());
    given(userService.update(eq(userId), any(UserUpdateRequest.class))).willReturn(updatedUser);

    // when & then
    mockMvc.perform(patch("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value("새닉네임"));
  }

  @Test
  @DisplayName("닉네임 수정 실패 테스트 - 존재하지 않는 사용자 404")
  void updateUser_Failure_NotFound() throws Exception {
    UUID notExistUserId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newNick");

    doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
        .when(userService).update(eq(notExistUserId), any(UserUpdateRequest.class));

    mockMvc.perform(patch("/api/users/{id}", notExistUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("닉네임 수정 실패 테스트 - 이미 존재하는 닉네임 409")
  void updateUser_DuplicateNickname() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("중복닉네임");

    doThrow(new BusinessException(ErrorCode.DUPLICATE_NICKNAME))
        .when(userService).update(eq(userId), any(UserUpdateRequest.class));

    // when & then
    mockMvc.perform(patch("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("DELETE /api/users/{id} - 논리삭제 성공 시 204 반환")
  void deleteSoft_Success() throws Exception {
    // given
    willDoNothing().given(userService).deleteSoft(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/users/{id} - 존재하지 않는 사용자 삭제 시 404 반환")
  void deleteSoft_Failure_NotFound() throws Exception {
    doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
        .when(userService).deleteSoft(userId);

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/users/{id}/hard - 물리삭제 성공 시 204 반환")
  void deleteHard_Success() throws Exception {
    // given
    willDoNothing().given(userService).deleteById(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{id}/hard", userId))
        .andExpect(status().isNoContent());
  }
}
