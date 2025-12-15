package com.lxp.aplus.payment.infrastructure.adapter;

import java.math.BigDecimal;

public record TossPaymentsConfirmRequest(
        String orderId,
        String paymentKey,
        BigDecimal amount
) {
    public static TossPaymentsConfirmRequest of(
            String orderId,
            String paymentKey,
            BigDecimal amount
    ) {
        return new TossPaymentsConfirmRequest(orderId, paymentKey, amount);
    }
}
