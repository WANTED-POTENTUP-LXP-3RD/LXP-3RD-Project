package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SectionErrorCode implements ErrorCode {
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "ES001", "해당 섹션을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
