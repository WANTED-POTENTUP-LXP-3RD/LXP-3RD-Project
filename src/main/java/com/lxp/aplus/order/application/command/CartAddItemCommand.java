package com.lxp.aplus.order.application.command;

import lombok.Builder;

@Builder
public record CartAddItemCommand(
        Long userId,
        Long courseId
) {
}
