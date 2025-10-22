package com.sprint.project.monew.user.controller;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserLoginRequest;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
import com.sprint.project.monew.user.service.AuthService;
import com.sprint.project.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<UserDto> create(@RequestBody UserRegisterRequest userRegisterRequest) {
    UserDto userDto = userService.create(userRegisterRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody UserLoginRequest userLoginRequest) {
    UserDto userDto = authService.login(userLoginRequest);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }
}
