package com.lxp.aplus.payment.application.usecase;

import com.lxp.aplus.payment.application.command.PreparePaymentCommand;
import com.lxp.aplus.payment.application.result.PaymentResult;

public interface PaymentCommandUseCase {

    /**
     * 결제 준비
     * - 주문 검증
     * - Payment 생성 (PENDING)
     */
    PaymentResult prepare(PreparePaymentCommand command);

    /**
     * 결제 완료(승인)
     * - Payment 승인
     * - Order 상태 변경
     */
    //void confirm(ConfirmPaymentCommand command);
}
