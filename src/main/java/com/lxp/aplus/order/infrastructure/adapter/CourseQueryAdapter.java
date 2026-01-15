package com.lxp.aplus.order.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.application.port.out.CourseSalesStatus;
import com.lxp.aplus.order.application.port.out.CourseSnapshot;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * [주문 도메인 연동 어댑터 - MSA 분리 가이드]
 * - 현재: 동일 프로세스 내 CourseRepository 직접 호출 (In-process)
 * - 전환 시:
 * 1) 동기식 REST 통신 필요 시: OpenFeign 또는 RestClient 적용
 * 2) 고성능/타입 안정성 필요 시: gRPC Stub 적용
 * 3) 어느 방식을 선택하든 CourseQueryPort의 인터페이스 정의는 유지함
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryAdapter implements CourseQueryPort {

    // FIXME: Course BC, User BC 침범 (의도됨) ⚠️
    // TODO: Repository 대신 HTTP/gRPC를 호출하는 외부 모듈 주입
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * 단일 강좌의 판매 가능 여부 확인
     * @param courseId 강좌 식별자
     * @return PUBLISHED 상태 여부
     */
    public boolean isCoursePublished(Long courseId) {
        return courseRepository.findById(courseId)
                .map(course -> course.getCourseStatus() == CourseStatus.PUBLISHED)
                .orElse(false);
    }

    /**
     * 다건 강좌의 판매 정보(가격, 상태) 일괄 조회
     * @param courseIds 강좌 식별자 리스트
     * @return 강좌 ID를 키로 하는 판매 정보 맵 (CourseSalesStatus)
     * @throws BusinessException 요청한 ID 중 하나라도 DB에 없을 경우 COURSE_NOT_FOUND 발생
     */
    public Map<Long, CourseSalesStatus> getCourseSalesStatusByIds(List<Long> courseIds) {

        List<Course> courses = courseRepository.findByIdIn(courseIds);

        if (courses.size() != courseIds.size()) {
            // TODO: 비즈니스 요구사항에 따라 누락된 ID 목록을 로깅하거나 예외 메시지에 포함하는 것 검토
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        course -> new CourseSalesStatus(course.getPrice(), course.getCourseStatus())
                ));
    }

    /*
     * 강좌의 구매 시점 상품 정보 조회
     */
    @Override
    public Map<Long, CourseSnapshot> getCourseSnapshot(List<Long> courseIds) {

        // 1. 강좌 조회
        List<Course> courses = courseRepository.findByIdIn(courseIds);

        // 2. instructorId 목록 추출
        List<Long> instructorIds = courses.stream()
                .map(Course::getInstructorId)
                .toList();
        // 3. 강사 조회
        List<User> instructors = userRepository.findByIdIn(instructorIds);

        // 4. id → User 매핑
        Map<Long, User> instructorMap = instructors.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 5. Course → CourseSnapshot 변환
        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        course -> {
                            User instructor = instructorMap.get(course.getInstructorId());

                            return CourseSnapshot.of(course, instructor);
                        }
                ));
    }
}
