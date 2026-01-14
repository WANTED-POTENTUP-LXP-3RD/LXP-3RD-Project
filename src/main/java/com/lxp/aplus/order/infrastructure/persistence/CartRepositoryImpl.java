package com.lxp.aplus.order.infrastructure.persistence;

import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartJpaRepository jpaRepository;

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Cart save(Cart cart) {
        return jpaRepository.save(cart);
    }
}
