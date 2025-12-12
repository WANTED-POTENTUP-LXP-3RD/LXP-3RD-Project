package com.lxp.aplus.payment.infrastructure.persistence;

import com.lxp.aplus.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, String>  {
    Optional<Payment> findByOrderId(String orderId);
}
