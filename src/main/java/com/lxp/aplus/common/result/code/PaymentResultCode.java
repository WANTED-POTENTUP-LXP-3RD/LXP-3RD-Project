package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentResultCode implements ResultCode {
    PAYMENT_PREPARE_SUCCESS(HttpStatus.CREATED, "SP001", "결제 준비가 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
