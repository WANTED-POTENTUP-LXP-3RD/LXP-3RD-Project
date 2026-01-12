package com.lxp.aplus.order.presentation.response;

import com.lxp.aplus.order.domain.Cart;
import lombok.Builder;

@Builder
public record CartRemoveItemResponse(
        Long cartId,
        Long removedCartItemId,
        int amount
) {
    public static CartRemoveItemResponse of(Cart cart, Long removedCartItemId, int amount) {

        return CartRemoveItemResponse.builder()
                .cartId(cart.getId())
                .removedCartItemId(removedCartItemId)
                .amount(amount)
                .build();
    }
}
