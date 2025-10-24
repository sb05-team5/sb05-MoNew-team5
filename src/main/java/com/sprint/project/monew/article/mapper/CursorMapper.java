package com.sprint.project.monew.article.mapper;

import com.sprint.project.monew.common.CursorPageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface CursorMapper {
    // Slice → SlicePageResponse
    default <T> CursorPageResponse<T> toSlice(Slice<T> slice) {
        String next = slice.hasNext() ? String.valueOf(slice.getNumber() + 1) : null;

        Instant nextAfter = null;
        if (slice.hasNext() && !slice.isEmpty()) {
            try {
                Object lastItem = slice.getContent().get(slice.getContent().size() - 1);
                var method = lastItem.getClass().getMethod("createdAt");
                Object result = method.invoke(lastItem);
                if (result instanceof Instant instant) {
                    nextAfter = instant;
                }
            } catch (Exception ignored) {
                // createdAt 없는 DTO인 경우 무시
            }
        }

        return CursorPageResponse.<T>builder()
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .nextCursor(next)
                .nextAfter(String.valueOf(nextAfter))
                .totalElements((long) slice.getNumberOfElements())
                .content(slice.getContent())
                .build();
    }


    // Slice → SlicePageResponse
    default <T> CursorPageResponse<T> toSlice(Slice<T> slice, Object nextCursor) {
        return CursorPageResponse.<T>builder()
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor.toString())
                .totalElements((long) slice.getNumberOfElements())
                .content(slice.getContent())
                .build();
    }

}
