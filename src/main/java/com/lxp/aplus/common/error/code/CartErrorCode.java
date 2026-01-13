package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_DUPLICATED_CART_ITEM(HttpStatus.CONFLICT, "EO001", "이미 담긴 항목입니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "EO002", "장바구니 항목이 존재하지 않습니다."),
    CART_CANNOT_ADD_UNPUBLISHED_COURSE(HttpStatus.BAD_REQUEST, "EO003", "현재 판매 중이지 않은 강좌는 장바구니에 담을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
