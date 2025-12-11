package com.lxp.aplus.payment.presentation.response;

import com.lxp.aplus.payment.domain.Payment;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentPrepareResponse(
        String orderId,
        BigDecimal amount
) {
    public static PaymentPrepareResponse from(Payment payment) {
        return PaymentPrepareResponse.builder()
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .build();
    }
}
