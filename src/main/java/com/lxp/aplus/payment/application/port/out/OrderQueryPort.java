package com.lxp.aplus.payment.application.port.out;

import com.lxp.aplus.payment.application.port.out.dto.PaymentOrderItemDto;

import java.util.List;

public interface OrderQueryPort {

    List<Long> getCourseIdsByOrderId(String orderId);

    List<PaymentOrderItemDto> getOrderItems(String orderId);
}
