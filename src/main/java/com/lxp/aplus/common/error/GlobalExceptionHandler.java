package com.lxp.aplus.common.error;

import com.lxp.aplus.common.error.code.GlobalErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Business Exception : [Code: {}] {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return makeErrorResponse(e.getErrorCode());
    }

    // 2. DTO 검증 오류 처리 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .orElse(GlobalErrorCode.VALIDATION_ERROR.getMessage());

        log.warn("Validation Exception : {}", message);
        return makeErrorResponse(GlobalErrorCode.VALIDATION_ERROR, message);
    }

    // 3. PathVariable / RequestParam 검증 실패 처리 (@Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .orElse(GlobalErrorCode.VALIDATION_ERROR.getMessage());

        log.warn("Constraint Violation : {}", message);
        return makeErrorResponse(GlobalErrorCode.VALIDATION_ERROR, message);
    }

    // 4. JSON 파싱 오류 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException e) {
        log.warn("JSON Parse Error : {}", e.getMessage());
        return makeErrorResponse(GlobalErrorCode.INVALID_JSON);
    }

    // 5. 필수 요청 파라미터 누락 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        String message = e.getParameterName() + " 파라미터가 필요합니다.";
        log.warn("Missing Parameter : {}", message);
        return makeErrorResponse(GlobalErrorCode.MISSING_PARAMETER, message);
    }

    // 6. 예상치 못한 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        log.error("Unexpected Error Occurred : ", e);
        return makeErrorResponse(GlobalErrorCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponse(ErrorCode errorCode) {
        return makeErrorResponse(errorCode, errorCode.getMessage());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus())
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }
}
