package com.sprint.project.monew.interest.dto;

import java.util.List;

public record InterestRegisterRequest(
    String name,
    List<String> keywords
) {

}
