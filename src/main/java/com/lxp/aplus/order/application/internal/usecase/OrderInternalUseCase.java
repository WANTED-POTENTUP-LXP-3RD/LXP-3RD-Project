package com.lxp.aplus.order.application.internal.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.OrderErrorCode;
import com.lxp.aplus.order.application.internal.dto.*;
import com.lxp.aplus.order.domain.OrderRepository;
import com.lxp.aplus.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderInternalUseCase {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderInternalResult getOrder(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        // Entity -> InternalDto 매핑
        var itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemInternalResult(
                        item.getItemId(),
                        item.getOrderItemId(),
                        item.getPrice()))
                .toList();

        // TODO: from static 메서드로 변경
        return new OrderInternalResult(order.getOrderId(), order.getUserId(), itemDtos);
    }
}