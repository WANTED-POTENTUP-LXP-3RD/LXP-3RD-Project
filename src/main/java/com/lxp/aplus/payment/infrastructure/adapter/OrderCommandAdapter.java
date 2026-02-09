// TODO: 삭제 예정
//package com.lxp.aplus.payment.infrastructure.adapter;
//
//import com.lxp.aplus.common.error.BusinessException;
//import com.lxp.aplus.common.error.code.CourseErrorCode;
//import com.lxp.aplus.common.error.code.OrderErrorCode;
//import com.lxp.aplus.course.domain.Course;
//import com.lxp.aplus.course.domain.CourseRepository;
//import com.lxp.aplus.order.application.port.out.dto.CoursePrice;
//import com.lxp.aplus.order.domain.OrderItem;
//import com.lxp.aplus.payment.application.port.out.dto.PaymentOrderDto;
//import com.lxp.aplus.order.domain.Order;
//import com.lxp.aplus.order.domain.OrderRepository;
//import com.lxp.aplus.payment.application.port.out.OrderCommandPort;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Transactional
//public class OrderCommandAdapter implements OrderCommandPort {
//
//    private final OrderRepository orderRepository;
//    private final CourseRepository courseRepository; // FIXME: Course BC 침범 (의도됨) ⚠️
//
//    public PaymentOrderDto createOrderFromCourseIds(Long userId, List<Long> courseIds) {
//
//        // 1. Course 가격 조회 (From Course BC)
//        List<CoursePrice> coursePrices = getCoursePriceByIds(courseIds);
//
//        // 2. OrderItem 리스트 생성
//        List<OrderItem> OrderItems = coursePrices.stream()
//                .map(OrderItem::create)
//                .toList();
//
//        // 3. Order 생성 및 저장
//        Order order = Order.create(userId, OrderItems);
//        orderRepository.save(order);
//
//        return PaymentOrderDto.of(order.getOrderId(), order.getAmount());
//    }
//
//    // TODO: 포트/어댑터 패턴으로 필요한 DTO로 변환하기 ----------
//    // NOTE: Course의 코드를 최대한 덜 건드리기 위해서 이곳에 임시로 작성한 코드입니다.
//    private List<CoursePrice> getCoursePriceByIds(List<Long> ids) {
//        List<Course> courses = courseRepository.findByIdIn(ids);
//
//        if (courses.size() != ids.size()) {
//            // TODO: 정확히 어떤 courseId가 없는지도 전달할 필요성 고민!
//            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
//        }
//
//        return courses.stream()
//                .map(course -> new CoursePrice(course.getId(), course.getPrice()))
//                .collect(Collectors.toList());
//    }
//    // --------------------------------------------------
//
//    public void completeOrder(String orderId, String approvedPaymentId, BigDecimal approvedAmount) {
//
//        // 1. Order 조회
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
//
//        // 2. Order 상태 변경
//        order.completeWithApprovedPayment(approvedPaymentId, approvedAmount);
//    }
//
//    public List<OrderItem> getOrderItemsOfOrder(String orderId) {
//
//        // 1. Order 조회
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
//
//        // 2. Order의 OrderItem 리스트 조회
//        return order.getOrderItems();
//    }
//}
