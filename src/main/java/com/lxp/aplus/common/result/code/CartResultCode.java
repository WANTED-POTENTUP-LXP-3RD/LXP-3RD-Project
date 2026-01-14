package com.lxp.aplus.common.result.code;


import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartResultCode implements ResultCode {

    CART_GET_ITEMS_SUCCESS(HttpStatus.OK, "SO001", "장바구니 목록 조회가 완료되었습니다."),
    CART_ADD_ITEM_SUCCESS(HttpStatus.CREATED, "SO002", "장바구니에 강좌가 추가되었습니다."),
    CART_REMOVE_ITEM_SUCCESS(HttpStatus.OK, "SO003", "장바구니에서 강좌가 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
