package com.lxp.aplus.payment.domain;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(String orderId);
}
