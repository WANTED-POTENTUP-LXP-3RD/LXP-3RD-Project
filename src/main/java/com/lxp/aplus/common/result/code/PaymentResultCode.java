package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentResultCode implements ResultCode {

    PAYMENT_PREPARE_SUCCESS(HttpStatus.CREATED, "SPM001", "결제 준비가 완료되었습니다."),
    PAYMENT_CONFIRM_SUCCESS(HttpStatus.OK, "SPM002", "결제가 정상적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
