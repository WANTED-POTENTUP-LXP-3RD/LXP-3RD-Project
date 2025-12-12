package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "EPM001", "주문 식별자가 유효하지 않거나 누락되었습니다."),
    PAYMENT_NOT_PENDING(HttpStatus.CONFLICT, "EPM002", "요청 작업은 결제 대기(PENDING) 상태에서만 수행 가능합니다."),
    PAYMENT_NOT_APPROVED(HttpStatus.CONFLICT, "EPM003", "요청 작업은 결제 승인(APPROVED) 상태에서만 수행 가능합니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.CONFLICT, "EPM004", "승인 요청된 금액이 결제 예상 금액과 일치하지 않습니다."),
    PAYMENT_ALREADY_APPROVED(HttpStatus.CONFLICT, "EPM005", "이미 승인 처리된 결제 건입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EPM006", "결제 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
