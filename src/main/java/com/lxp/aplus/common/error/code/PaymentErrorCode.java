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
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EPM006", "결제 정보를 찾을 수 없습니다."),
    // NOTE: 어댑터가 PG API를 호출했을 때, 토스 서버로부터 400번대 오류
    PAYMENT_CONFIRM_ERROR(HttpStatus.BAD_REQUEST, "EPM007", "결제 승인 요청 정보가 올바르지 않거나 이미 처리되어 결제가 실패했습니다. 주문 정보를 확인해 주세요."),
    // NOTE: 어댑터가 PG API를 호출했을 때, 토스 서버로부터 500번대 오류 -> 클라이언트에는 400 BAD_REQUEST로 일관적으로 처리
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_REQUEST, "EPM008", "결제 시스템 내부 오류로 인해 결제 처리에 실패했습니다. 잠시 후 다시 시도해 주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
