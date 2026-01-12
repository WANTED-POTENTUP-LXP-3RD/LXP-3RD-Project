package com.lxp.aplus.order.presentation.response;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CartErrorCode;
import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartAddItemResponse(
        Long cartId,
        Long cartItemId,
        BigDecimal amount
) {
    public static CartAddItemResponse of(Cart cart, Long addedCourseId) {

        // 리스트에서 해당 courseId를 가진 항목을 찾음
        CartItem addedCartItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getCourseId().equals(addedCourseId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND));

        return CartAddItemResponse.builder()
                .cartId(cart.getId())
                .cartItemId(addedCartItem.getId())  // DB에서 받아온 ID
                .amount(cart.calculateTotalAmount())
                .build();
    }

}
