package com.lxp.aplus.order.application.internal.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.OrderErrorCode;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.application.port.out.dto.CoursePrice;
import com.lxp.aplus.order.application.internal.dto.*;
import com.lxp.aplus.order.domain.OrderItem;
import com.lxp.aplus.order.domain.OrderRepository;
import com.lxp.aplus.order.domain.Order;
import com.lxp.aplus.payment.application.port.out.dto.PaymentOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderInternalUseCase {

    private final OrderRepository orderRepository;
    private final CourseQueryPort courseQueryPort;

    @Transactional(readOnly = true)
    public OrderInternalResult getOrder(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderItemInternalResult> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemInternalResult(
                        item.getItemId(),
                        item.getOrderItemId(),
                        item.getPrice()))
                .toList();

        return new OrderInternalResult(order.getOrderId(), order.getUserId(), orderItems);
    }

    public OrderInternalResult createOrder(Long userId, List<Long> courseIds) {

        // 1. Course 가격 조회
        List<CoursePrice> coursePrices = courseQueryPort.getCoursePriceByIds(courseIds);

        // 2. OrderItem 리스트 생성
        List<OrderItem> OrderItems = coursePrices.stream()
                .map(OrderItem::create)
                .toList();

        // 3. Order 생성 및 저장
        Order order = Order.create(userId, OrderItems);
        orderRepository.save(order);

        return PaymentOrderDto.of(order.getOrderId(), order.getAmount());
    }
}