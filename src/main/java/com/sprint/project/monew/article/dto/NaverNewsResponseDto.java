package com.sprint.project.monew.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class NaverNewsResponseDto {
    private List<NewsItemDto> items;

}
