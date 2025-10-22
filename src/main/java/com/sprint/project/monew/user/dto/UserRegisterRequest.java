package com.sprint.project.monew.user.dto;

public record UserRegisterRequest(
  String email,
  String nickname,
  String password
) {
}
