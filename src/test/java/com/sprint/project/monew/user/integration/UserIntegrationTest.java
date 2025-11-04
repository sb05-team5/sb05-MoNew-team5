package com.sprint.project.monew.user.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.service.UserService;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("사용자 생성 통합 테스트")
  void creatUser_Success() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "test@email.com",
        "테스터",
        "qwer1234!"
    );

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.email", is("test@email.com")))
        .andExpect(jsonPath("$.nickname", is("테스터")));
  }

  @Test
  @DisplayName("사용자 생성 실패 통합 테스트 - 유효하지 않은 요청")
  void createUser_Failure_InvalidRequest() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "u",
        "invalid-email",
        ""
    );

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 로그인 통합 테스트")
  void loginUser_Success() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "login@test.com", "로그인유저", "qwer1234!"
    );
    userService.create(registerRequest);

    UserLoginRequest loginRequest = new UserLoginRequest(
        "login@test.com", "qwer1234!"
    );

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(header().exists("MoNew-Request-User-ID"))
        .andExpect(jsonPath("$.email", is("login@test.com")))
        .andExpect(jsonPath("$.nickname", is("로그인유저")));
  }

  @Test
  @DisplayName("사용자 닉네임 변경 테스트")
  void udpateUser_Success() throws Exception {
    UserRegisterRequest originUser = new UserRegisterRequest(
        "test1@test.com",
        "기존닉네임",
        "qwer1234!"
    );
    UserDto createUser = userService.create(originUser);

    UserUpdateRequest request = new UserUpdateRequest("새로운닉네임");

    mockMvc.perform(patch("/api/users/{id}", createUser.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(createUser.id().toString())))
        .andExpect(jsonPath("$.nickname", is("새로운닉네임")));
  }

  @Test
  @DisplayName("사용자 논리 삭제 테스트")
  void deleteUser_Soft_Success() throws Exception {
    UserRegisterRequest originUser = new UserRegisterRequest(
        "soft@test.com", "삭제유저", "qwer1234!"
    );
    UserDto user = userService.create(originUser);

    mockMvc.perform(delete("/api/users/{id}", user.id()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 물리 삭제 테스트")
  void deleteUser_Hard_Success() throws Exception {
    UserRegisterRequest originUser = new UserRegisterRequest(
        "hard@test.com", "삭제유저", "qwer1234!"
    );
    UserDto user = userService.create(originUser);

    mockMvc.perform(delete("/api/users/{id}/hard", user.id()))
        .andExpect(status().isNoContent());
  }
}
