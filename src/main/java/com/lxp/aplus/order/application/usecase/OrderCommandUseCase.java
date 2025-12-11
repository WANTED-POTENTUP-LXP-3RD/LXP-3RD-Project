package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
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

    // TODO: 포트/어댑터 패턴으로 필요한 DTO로 변환하기 ----------
    // NOTE: Course의 코드를 최대한 덜 건드리기 위해서 이곳에 임시로 작성한 코드입니다.
    private List<CoursePrice> getCoursePriceByIds(List<Long> ids) {
        List<Course> courses = courseRepository.findByIdIn(ids);

        if (courses.size() != ids.size()) {
            // TODO: 정확히 어떤 courseId가 없는지도 전달할 필요성 고민!
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        return courses.stream()
                .map(course -> new CoursePrice(course.getId(), course.getPrice()))
                .collect(Collectors.toList());
    }
    // --------------------------------------------------
}
