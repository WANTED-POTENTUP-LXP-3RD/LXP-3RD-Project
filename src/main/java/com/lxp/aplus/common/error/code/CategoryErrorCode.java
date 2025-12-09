package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EC001", "해당 카테고리를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
