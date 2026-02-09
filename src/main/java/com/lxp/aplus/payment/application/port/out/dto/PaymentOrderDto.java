package com.lxp.aplus.payment.application.port.out.dto;

import java.math.BigDecimal;

public record PaymentOrderDto(
    String orderId,
    BigDecimal amount
) {
    public static PaymentOrderDto of(String orderId, BigDecimal amount) {
        return new PaymentOrderDto(orderId, amount);
    }
}
