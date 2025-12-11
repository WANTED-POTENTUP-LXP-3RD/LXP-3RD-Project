package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SectionErrorCode implements ErrorCode {
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "ES001", "해당 섹션을 찾을 수 없습니다."),
    SECTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ES002", "해당 섹션에 대한 접근 권한이 없습니다."),
    SECTION_ORDER_DUPLICATED(HttpStatus.BAD_REQUEST, "ES003", "이미 존재하는 섹션 순서입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
