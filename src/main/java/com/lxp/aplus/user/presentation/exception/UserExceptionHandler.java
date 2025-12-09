package com.lxp.aplus.user.presentation.exception;

import com.lxp.aplus.common.error.ErrorCode;
import com.lxp.aplus.common.error.ErrorResponse;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import com.lxp.aplus.common.error.code.UserErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * User 도메인 전용 Exception Handler
 * 
 * User 관련 Controller의 validation 에러를 처리.
 * basePackages를 지정하여 User 도메인만 처리하도록 제한.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.lxp.aplus.user.presentation.controller")
public class UserExceptionHandler {

    /**
     * User 관련 DTO 검증 오류 처리 (@Valid)
     * 
     * User 도메인의 validation 에러를 UserErrorCode로 반환.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        FieldError firstError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        if (firstError == null) {
            return makeErrorResponse(GlobalErrorCode.VALIDATION_ERROR);
        }

        // 어노테이션 타입에 따라 다른 에러 코드 반환
        ErrorCode errorCode = determineErrorCode(firstError);
        String message = firstError.getField() + " : " + firstError.getDefaultMessage();

        log.warn("User Validation Exception : {}", message);
        return makeErrorResponse(errorCode, message);
    }

    /**
     * FieldError의 어노테이션 타입과 필드명에 따라 적절한 UserErrorCode 반환
     */
    private ErrorCode determineErrorCode(FieldError fieldError) {
        String errorCode = fieldError.getCode();
        String fieldName = fieldError.getField();

        if (fieldName == null) {
            return GlobalErrorCode.VALIDATION_ERROR;
        }

        String lowerFieldName = fieldName.toLowerCase();

        // @Email 어노테이션 검증 실패
        if ("Email".equals(errorCode)) {
            return UserErrorCode.INVALID_EMAIL;
        }

        // @Pattern 어노테이션 검증 실패
        if ("Pattern".equals(errorCode)) {
            if (lowerFieldName.contains("phone")) {
                return UserErrorCode.INVALID_PHONE_NUMBER;
            }
            if (lowerFieldName.contains("password")) {
                return UserErrorCode.INVALID_PASSWORD;
            }
            // 기타 Pattern 검증 실패
            return GlobalErrorCode.VALIDATION_ERROR;
        }

        // @NotBlank 어노테이션 검증 실패 - fieldName에 따라 구체적인 에러 코드 반환
        if ("NotBlank".equals(errorCode)) {
            if (lowerFieldName.contains("email")) {
                return UserErrorCode.INVALID_EMAIL;
            }
            if (lowerFieldName.contains("password")) {
                return UserErrorCode.INVALID_PASSWORD;
            }
            if (lowerFieldName.contains("phone")) {
                return UserErrorCode.INVALID_PHONE_NUMBER;
            }
            // 기타 필드의 NotBlank 검증 실패
            return GlobalErrorCode.VALIDATION_ERROR;
        }

        // @NotNull 어노테이션 검증 실패 - fieldName에 따라 구체적인 에러 코드 반환
        if ("NotNull".equals(errorCode)) {
            if (lowerFieldName.contains("email")) {
                return UserErrorCode.INVALID_EMAIL;
            }
            if (lowerFieldName.contains("password")) {
                return UserErrorCode.INVALID_PASSWORD;
            }
            if (lowerFieldName.contains("phone")) {
                return UserErrorCode.INVALID_PHONE_NUMBER;
            }
            // 기타 필드의 NotNull 검증 실패
            return GlobalErrorCode.VALIDATION_ERROR;
        }

        // 기타 validation 에러 - fieldName에 따라 구체적인 에러 코드 반환
        if (lowerFieldName.contains("email")) {
            return UserErrorCode.INVALID_EMAIL;
        }
        if (lowerFieldName.contains("password")) {
            return UserErrorCode.INVALID_PASSWORD;
        }
        if (lowerFieldName.contains("phone")) {
            return UserErrorCode.INVALID_PHONE_NUMBER;
        }

        return GlobalErrorCode.VALIDATION_ERROR;
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

