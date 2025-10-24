package com.sprint.project.monew.common;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Boolean hasNext
) {
}