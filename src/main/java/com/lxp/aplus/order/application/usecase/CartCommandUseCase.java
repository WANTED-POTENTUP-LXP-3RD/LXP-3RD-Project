package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.order.application.command.CartAddItemCommand;
import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartRepository;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandUseCase {

    private final CartRepository cartRepository;

    /*
     *
     */
    public CartAddItemResponse addCartItemToCart(CartAddItemCommand command) {

        // 1. cart 조회
        Cart cart = getCart(command.userId());

        // 2. cart에 cartItem 추가
        cart.addCartItem(command.courseId());

        // 3. 명시적으로 save 호출 -> JPA가 id(PK) 채워줌
        cartRepository.save(cart);

        return CartAddItemResponse.of(cart, command.courseId());
    }

    /*
     *
     */
    public void removeCartItemFromCart(Long userId, Long courseId) {
        Cart cart = getCart(userId);
        cart.removeCartItem(courseId);
    }

    /*
     *
     */
    private Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));

    }
}
