package com.lxp.aplus.payment.infrastructure.adapter;

import java.math.BigDecimal;

public record TossPaymentsConfirmResponse(
        String paymentKey,
        String status,
        BigDecimal totalAmount
) {
}
