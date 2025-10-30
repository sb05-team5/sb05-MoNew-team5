package com.sprint.project.monew.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;



@Builder
public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        String nextAfter,
        Integer size,
        Boolean hasNext,
        Integer totalElements

) {
}