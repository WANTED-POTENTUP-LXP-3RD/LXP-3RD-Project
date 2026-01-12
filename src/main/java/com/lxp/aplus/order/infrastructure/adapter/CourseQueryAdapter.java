package com.lxp.aplus.order.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryAdapter implements CourseQueryPort {

    // FIXME: Course BC 침범 (의도됨) ⚠️
    // TODO: Repository 대신 HTTP/gRPC를 호출하는 외부 모듈 주입
    private final CourseRepository courseRepository;

    /*
     * [MSA 분리 가이드]
     * - 현재: 동일 프로세스 내 CourseRepository 직접 호출 (In-process)
     * - 전환 시:
     * 1) 동기식 REST 통신 필요 시: OpenFeign 또는 RestClient 적용
     * 2) 고성능/타입 안정성 필요 시: gRPC Stub 적용
     * 3) 어느 방식을 선택하든 CourseQueryPort의 인터페이스 정의는 유지함
     */
    public Map<Long, Integer> getCoursePriceByIds(List<Long> ids) {

        List<Course> courses = courseRepository.findByIdIn(ids);

        if (courses.size() != ids.size()) {
            // TODO: 비즈니스 요구사항에 따라 누락된 ID 목록을 로깅하거나 예외 메시지에 포함하는 것 검토
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        Course::getPrice
                ));
    }
}
