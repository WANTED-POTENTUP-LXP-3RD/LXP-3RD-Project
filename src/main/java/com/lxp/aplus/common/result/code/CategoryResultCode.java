package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryResultCode implements ResultCode {
    CATEGORY_LIST_SUCCESS(HttpStatus.OK, "SCT001", "카테고리 목록을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
