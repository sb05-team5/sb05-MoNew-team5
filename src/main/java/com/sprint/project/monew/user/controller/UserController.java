package com.sprint.project.monew.user.controller;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.dto.UserUpdateRequest;
import com.sprint.project.monew.user.service.AuthService;
import com.sprint.project.monew.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @PostMapping
  public ResponseEntity<UserDto> create(
      @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
    UserDto userDto = userService.create(userRegisterRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
    UserDto userDto = authService.login(userLoginRequest);
    UUID userId = userDto.id();
    return ResponseEntity
        .status(HttpStatus.OK)
        .header("MoNew-Request-User-ID", userId.toString())
        .body(userDto);
  }

  @PatchMapping(path = "{userId}")
  public ResponseEntity<UserDto> update(@PathVariable("userId") UUID userId,
      @RequestBody @Valid UserUpdateRequest request) {
    UserDto userDto = userService.update(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @DeleteMapping(path = "{userId}")
  public ResponseEntity<UserDto> deleteSoft(@PathVariable("userId") UUID userId) {
    userService.deleteSoft(userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "{userId}/hard")
  public ResponseEntity<UserDto> deleteHard(@PathVariable("userId") UUID userId) {
    userService.deleteById(userId);
    return ResponseEntity.noContent().build();
  }
}
