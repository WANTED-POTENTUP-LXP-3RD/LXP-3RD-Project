package com.lxp.aplus.order.application.command;

import lombok.Builder;

@Builder
public record CartRemoveItemCommand(
        Long userId,
        Long cartItemId
) {
    public static CartRemoveItemCommand of(Long userId, Long cartItemId) {
        return new CartRemoveItemCommand(userId, cartItemId);
    }
}
