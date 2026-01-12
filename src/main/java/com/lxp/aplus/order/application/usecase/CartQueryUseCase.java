package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.order.domain.CartItem;
import com.lxp.aplus.order.domain.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartQueryUseCase {

    private final CartRepository cartRepository;

    //public List<CartItem> getCartItems(Long userId) {
    //
    //}
}
