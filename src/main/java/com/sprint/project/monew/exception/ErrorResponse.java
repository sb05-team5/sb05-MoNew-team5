package com.sprint.project.monew.exception;

import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String exceptionType;
    private String path;
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static ErrorResponse of(int status, String message, String type, String path) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .exceptionType(type)
                .path(path)
                .build();
    }
}