package com.lxp.aplus.order.application.result;

import java.math.BigDecimal;

public record OrderCreateResult(
    String orderId,
    BigDecimal amount
) {
    public static OrderCreateResult of(String orderId, BigDecimal amount) {
        return new OrderCreateResult(orderId, amount);
    }
}
