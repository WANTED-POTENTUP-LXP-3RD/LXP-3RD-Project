package com.lxp.aplus.order.infrastructure.persistence;

import com.lxp.aplus.order.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);
}