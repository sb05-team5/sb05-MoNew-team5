package com.sprint.project.monew.article.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ArticleRestoreResultDto(
        Instant restoreDate,
        List<String> restoredArticleIds,
        Integer restoredArticleCount

) { }
