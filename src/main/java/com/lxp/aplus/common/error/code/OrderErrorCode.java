package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_INVALID_STATUS(HttpStatus.CONFLICT, "EO001", "해당 주문은 이미 처리 중이거나 완료되어 요청 작업을 수행할 수 없습니다."),
    ORDER_ALREADY_PAID(HttpStatus.CONFLICT, "EO002", "이미 해당 주문에 대해 승인된 결제가 존재합니다."),
    ORDER_AMOUNT_MISMATCH(HttpStatus.CONFLICT, "EO003", "주문 총액과 실제 승인된 결제 금액이 일치하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "EO004", "주문 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
