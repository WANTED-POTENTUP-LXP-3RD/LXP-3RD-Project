package com.lxp.aplus.order.domain;

import java.util.Optional;

public interface CartRepository {

    Optional<Cart> findByUserId(Long userId);

    Cart save(Cart cart);
}
