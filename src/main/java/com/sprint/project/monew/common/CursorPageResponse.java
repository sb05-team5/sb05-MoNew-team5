package com.sprint.project.monew.common;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor,
    String nextAfter,
    Integer size,
    Integer totalElements,
    Boolean hasNext
) {
}