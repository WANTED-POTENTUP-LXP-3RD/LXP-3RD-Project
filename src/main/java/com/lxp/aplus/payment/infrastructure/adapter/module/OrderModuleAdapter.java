package com.lxp.aplus.payment.infrastructure.adapter.module;

import com.lxp.aplus.order.application.internal.dto.OrderInternalResult;
import com.lxp.aplus.order.application.internal.dto.OrderItemInternalResult;
import com.lxp.aplus.order.application.internal.usecase.OrderInternalUseCase;
import com.lxp.aplus.payment.application.port.out.OrderQueryPort;
import com.lxp.aplus.payment.application.port.out.dto.PaymentOrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderModuleAdapter implements OrderQueryPort {

    private final OrderInternalUseCase orderInternalUseCase;

    // --- 조회 ---
    @Override
    public List<Long> getCourseIdsByOrderId(String orderId) {

        OrderInternalResult order = orderInternalUseCase.getOrder(orderId);

        return order.items().stream()
                .map(OrderItemInternalResult::itemId)
                .toList();
    }

    @Override
    public List<PaymentOrderItemDto> getOrderItems(String orderId) {

        OrderInternalResult order = orderInternalUseCase.getOrder(orderId);

        return order.items().stream()
                .map(item -> new PaymentOrderItemDto(item.itemId(), item.orderItemId()))
                .toList();
    }

    // --- 명령 ---
}
