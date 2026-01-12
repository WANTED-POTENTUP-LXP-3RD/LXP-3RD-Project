package com.lxp.aplus.order.presentation.request;

import com.lxp.aplus.order.application.command.CartAddItemCommand;
import jakarta.validation.constraints.NotNull;

public record CartAddItemRequest(
        @NotNull(message = "")
        Long courseId
) {
    public CartAddItemCommand toCommand(Long userId) {
        return CartAddItemCommand.builder()
                .userId(userId)
                .courseId(this.courseId)
                .build();
    }
}
