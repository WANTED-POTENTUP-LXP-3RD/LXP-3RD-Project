package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ErrorCode {
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "EC001", "해당 강좌를 찾을 수 없습니다."),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "EC002", "해당 강좌에 대한 접근 권한이 없습니다."),
    CANNOT_MODIFY_PUBLISHED_COURSE(HttpStatus.BAD_REQUEST, "EC003", "이미 발행된 강좌는 수정하거나 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
