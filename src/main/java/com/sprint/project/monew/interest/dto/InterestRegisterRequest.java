package com.sprint.project.monew.interest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestRegisterRequest(
    @NotBlank(message = "관심사 입력은 필수입니다.")
    @Size(min = 1, max = 50, message = "1자 이상 50자 이하로 입력해주세요.")
    String name,

    @NotNull
    @Size(min = 1, max = 10, message = "키워드는 1개 이상 10개 이하로 입력해주세요.")
    List<@NotBlank(message = "키워드 입력은 필수입니다.") String> keywords
) {

}
