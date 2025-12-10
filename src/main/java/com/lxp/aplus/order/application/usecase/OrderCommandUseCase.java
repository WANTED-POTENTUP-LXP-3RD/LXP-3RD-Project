package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.order.application.CoursePrice;
import com.lxp.aplus.order.application.result.OrderCreateResult;
import com.lxp.aplus.order.domain.Order;
import com.lxp.aplus.order.domain.OrderLine;
import com.lxp.aplus.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandUseCase {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository; // FIXME: Course BC 침범 (의도됨) ⚠️

    public OrderCreateResult createOrderFromCourseIds(Long userId, List<Long> courseIds) {

        // 1. Course 가격 조회 (From Course BC)
        // TODO: 이벤트로 수정
        List<CoursePrice> coursePrices = getCoursePriceByIds(courseIds);

        // 2. OrderLine 리스트 생성
        List<OrderLine> orderLines = coursePrices.stream()
                .map(OrderLine::from)
                .collect(Collectors.toList());

        // 3. Order 생성 및 저장
        Order order = Order.create(userId, orderLines);
        orderRepository.save(order);

        return OrderCreateResult.of(order.getOrderId(), order.getAmount());
    }

    // TODO: 이벤트 기반으로 분리 ----------
    private List<CoursePrice> getCoursePriceByIds(List<Long> ids) {
        List<Course> courses = courseRepository.findByIdIn(ids);

        if (courses.size() != ids.size()) {
            throw new IllegalArgumentException("일부 강의를 찾을 수 없습니다.");
        }

        return courses.stream()
                .map(course -> new CoursePrice(course.getId(), course.getPrice()))
                .collect(Collectors.toList());
    }
    // --------------------------------
}
