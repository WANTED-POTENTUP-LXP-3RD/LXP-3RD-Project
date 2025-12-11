package com.lxp.aplus.payment.infrastucture.persistence;

import com.lxp.aplus.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, String>  {
}
