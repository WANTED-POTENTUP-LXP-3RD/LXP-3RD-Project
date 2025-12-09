package com.lxp.aplus.user.domain.exception;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU001", "사용자를 찾을 수 없습니다."),
    ROLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EU002", "이미 존재하는 역할입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "EU003", "유효하지 않은 상태 변경입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

