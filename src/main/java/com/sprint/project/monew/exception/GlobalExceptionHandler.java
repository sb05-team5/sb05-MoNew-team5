package com.sprint.project.monew.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    log.error("예상치 못한 오류 발생 : {}", ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    log.error("커스텀 예외 발생 : code = {}, message = {}", ex.getErrorCode(), ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(ex, ex.getErrorCode().getStatus().value());
    return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    log.warn("요청 유효성 검사 실패 : {}", ex.getMessage());
    Map<String, Object> validationErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

    BusinessException validationException = new BusinessException(ErrorCode.VALIDATION_ERROR);
    validationErrors.forEach(validationException::addDetail);

    ErrorResponse errorResponse = new ErrorResponse(
        validationException,
        ErrorCode.VALIDATION_ERROR.getStatus().value()
    );
    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus()).body(errorResponse);
  }
}

