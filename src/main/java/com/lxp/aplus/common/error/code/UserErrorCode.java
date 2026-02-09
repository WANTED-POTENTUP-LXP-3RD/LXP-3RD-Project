package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU001", "사용자를 찾을 수 없습니다."),
    ROLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EU002", "이미 존재하는 역할입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "EU003", "유효하지 않은 상태 변경입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "EU004", "올바른 이메일 형식이 아닙니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "EU005", "올바른 전화번호 형식이 아닙니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "EU006", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "EU007", "비밀번호가 올바르지 않습니다."),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "EU008", "활성화되지 않은 사용자입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EU009", "유효하지 않은 Refresh Token입니다."),
    NOT_INSTRUCTOR(HttpStatus.FORBIDDEN, "EU010", "강사 권한이 필요합니다."),
    INSTRUCTOR_APPLICATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EU011", "이미 강사 신청이 존재합니다."),
    INSTRUCTOR_APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "EU013", "강사 신청을 찾을 수 없습니다."),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "EU014", "관리자 권한이 필요합니다."),
    INSTRUCTOR_APPLICATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "EU015", "이미 처리된 강사 신청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
