package com.lxp.aplus.payment.application.port.out;

import com.lxp.aplus.order.domain.OrderItem;
import com.lxp.aplus.payment.application.result.OrderCreateResult;

import java.math.BigDecimal;
import java.util.List;

public interface OrderCommandPort {

    /*
     * 주문 생성 (PENDING)
     */
    OrderCreateResult createOrderFromCourseIds(Long userId, List<Long> courseIds);

    /*
     * 주문 완료 (COMPLETED)
     */
    void completeOrder(String orderId, String approvedPaymentId, BigDecimal approvedAmount);

    /*
     * 주문ID로 주문 항목 조회
     */
    List<OrderItem> getOrderItemsOfOrder(String orderId);
}
