package com.lxp.aplus.payment.application.port.out;

import java.math.BigDecimal;

public interface PaymentGatewayPort {
    /**
     * PG사에 최종 결제 승인을 요청합니다.
     * @param orderId 주문 ID
     * @param paymentKey PG사가 발행한 결제 키
     * @param amount 최종 결제 금액
     * @throws BusinessException PG사 통신 또는 승인 실패 시 발생
     */
    void confirmPayment(String orderId, String paymentKey, BigDecimal amount);
}
