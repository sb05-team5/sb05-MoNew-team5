package com.sprint.project.monew.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email
    String email,
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하여야 합니다")
    String nickname,
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{6,20}$",
            message = "비밀번호는 영문과 숫자, 특수문자를 포함해 6자 이상 20자 이하여야 합니다")
    String password
) {

}
