package com.sprint.project.monew.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewsItemDto {

    private String title;
    private String originallink;
    private String link;
    private String description;

    @JsonProperty("pubDate")
    private String publishDate;
}
