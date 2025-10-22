package com.sprint.project.monew.user.controller;

import com.sprint.project.monew.user.dto.UserDto;
import com.sprint.project.monew.user.dto.UserRegisterRequest;
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

  @PostMapping
  public ResponseEntity<UserDto> create(@RequestBody UserRegisterRequest userRegisterRequest) {
    System.out.println("요청 body: " + userRegisterRequest);
    UserDto userDto = userService.create(userRegisterRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }
}
