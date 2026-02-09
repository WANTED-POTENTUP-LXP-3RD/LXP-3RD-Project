package com.lxp.aplus.order.application.internal.dto;

import java.math.BigDecimal;

public record OrderItemInternalResult(
        Long itemId,        // courseId
        Long orderItemId,   // PK
        BigDecimal price
) {
}
