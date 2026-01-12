package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_DUPLICATED_CART_ITEM(HttpStatus.CONFLICT, "EO001", "이미 담긴 아이템입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
