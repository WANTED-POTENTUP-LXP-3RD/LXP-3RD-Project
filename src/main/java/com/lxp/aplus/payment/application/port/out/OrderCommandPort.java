package com.lxp.aplus.payment.application.port.out;

import com.lxp.aplus.payment.application.port.out.dto.PaymentOrderDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderCommandPort {

    PaymentOrderDto createOrder(Long userId, List<Long> courseIds);

    void completeOrder(String orderId, String approvedPaymentId, BigDecimal approvedAmount);
}
