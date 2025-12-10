package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "EG001", "잘못된 요청입니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "EG002", "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "EG003", "필수 요청 파라미터가 누락되었습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EG004", "예기치 못한 오류가 발생했습니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "EG005", "유효하지 않은 인자값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "EG006", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "EG007", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "EG008", "Access Token이 만료되었습니다. 다시 로그인해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
