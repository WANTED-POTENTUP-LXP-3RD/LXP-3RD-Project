package com.lxp.aplus.order.application.internal.dto;

import java.util.List;

public record OrderInternalResult(
        String orderId,
        Long userId,
        List<OrderItemInternalResult> items
) {
}
