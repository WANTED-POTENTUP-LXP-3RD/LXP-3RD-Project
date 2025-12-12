package com.lxp.aplus.payment.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.OrderErrorCode;
import com.lxp.aplus.order.domain.Order;
import com.lxp.aplus.order.domain.OrderLine;
import com.lxp.aplus.order.domain.OrderRepository;
import com.lxp.aplus.payment.application.port.out.OrderQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderQueryPort의 구현체 (Adapter)
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryAdapter implements OrderQueryPort {

    // 💡 Order BC의 Repository를 주입받아 Order Aggregate Root에 접근합니다.
    private final OrderRepository orderRepository;

    @Override
    public List<Long> getCourseIdsByOrderId(String orderId) {

        // 1. Order Aggregate Root 조회
        // Order는 OrderLine 컬렉션을 가지고 있으므로, fetch join 등으로 함께 로딩되어야 합니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        // 2. OrderLine 목록에서 courseId 추출 및 반환
        return order.getOrderLines().stream()
                .map(OrderLine::getItemId)
                .collect(Collectors.toList());
    }
}
