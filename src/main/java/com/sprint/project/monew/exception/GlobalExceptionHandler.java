package com.sprint.project.monew.exception;

import com.sprint.project.monew.exception.ErrorResponse;
import com.sprint.project.monew.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ✅ 400 Bad Request — 잘못된 요청 */
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest req) {
        log.warn("[400] Bad Request: {}", e.getMessage());
        String message = extractMessage(e) + " 잘못된 요청 입력값 검증 실패)";
        ErrorResponse error = ErrorResponse.of(400, message, e.getClass().getSimpleName(), req.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    /* ✅ 404 Not Found — 리소스 없음 */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e, HttpServletRequest req) {
        log.warn("[404] Resource Not Found: 사용자 정보 없음 : {}", e.getMessage());
        ErrorResponse error = ErrorResponse.of(404, e.getMessage(), e.getClass().getSimpleName(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* ✅ (선택) 매핑 없는 URL 요청 시 404 JSON 반환 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException e, HttpServletRequest req) {
        String message = "요청한 경로를 찾을 수 없습니다: " + req.getRequestURI();
        ErrorResponse error = ErrorResponse.of(404, message, e.getClass().getSimpleName(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* ✅ 500 Internal Server Error — 나머지 모든 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception e, HttpServletRequest req) {
        log.error("[500] Internal Server Error: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.of(500, "서버 내부 오류", e.getClass().getSimpleName(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /* ✅ 메시지 추출 유틸 */
    private String extractMessage(Exception e) {
        if (e instanceof MethodArgumentNotValidException ex) {
            return ex.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .findFirst().orElse("요청이 올바르지 않습니다.");
        }
        if (e instanceof ConstraintViolationException ex) {
            return ex.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .findFirst().orElse("요청이 올바르지 않습니다.");
        }
        return e.getMessage() != null ? e.getMessage() : "요청이 올바르지 않습니다.";
    }
}
