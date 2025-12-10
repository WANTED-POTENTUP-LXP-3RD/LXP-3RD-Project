package com.lxp.aplus.order.application.result;

import java.math.BigDecimal;

public record OrderCreationResult(
    String orderId,
    BigDecimal amount
) {
    public static OrderCreationResult of(String orderId, BigDecimal amount) {
        return new OrderCreationResult(orderId, amount);
    }
}
