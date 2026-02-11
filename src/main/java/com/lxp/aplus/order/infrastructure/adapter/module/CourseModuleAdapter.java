package com.lxp.aplus.order.infrastructure.adapter.module;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.internal.dto.CourseInternalResult;
import com.lxp.aplus.course.application.internal.usecase.CourseInternalUseCase;
import com.lxp.aplus.course.domain.CourseStatus;  // FIXME: course domain 침범. 응답 DTO 바뀌어야 해서 일단 냅둠 🚨
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.application.port.out.dto.CourseSalesStatus;
import com.lxp.aplus.order.application.port.out.dto.CoursePrice;
import com.lxp.aplus.order.application.port.out.dto.CourseSnapshot;
import com.lxp.aplus.user.application.internal.dto.UserInternalResult;
import com.lxp.aplus.user.application.internal.usecase.UserInternalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseModuleAdapter implements CourseQueryPort {

    private final CourseInternalUseCase courseInternalUseCase;
    private final UserInternalUseCase userInternalUseCase;

    /*
     * 강좌 판매 가능 여부 조회
     */
    public boolean isCourseAvailableForSale(Long courseId) {

        return courseInternalUseCase.findById(courseId)
                .map(course -> CourseStatus.PUBLISHED.equals(course.courseStatus()))
                .orElse(false);
    }

    /*
     * 강좌 판매 상태(가격, 상태) 조회
     * @param courseIds 강좌 식별자 리스트
     * @return Map<courseId, CourseSalesStatus>
     */
    public Map<Long, CourseSalesStatus> getCourseSalesStatusByIds(List<Long> courseIds) {

        Map<Long, CourseInternalResult> courses = courseInternalUseCase.getCoursesByIds(courseIds);

        if (courses.size() != courseIds.size()) {
            // TODO: 비즈니스 요구사항에 따라 누락된 ID 목록을 로깅하거나 예외 메시지에 포함하는 것 검토
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        return courses.values().stream()
                .collect(Collectors.toMap(
                        CourseInternalResult::id,
                        course -> new CourseSalesStatus(course.price(), course.courseStatus())
                ));
    }

    /*
     * 강좌의 상품 정보 조회
     * @param courseIds 강좌 식별자 리스트
     * @return Map<courseId, CourseSnapshot>
     */
    @Override
    public Map<Long, CourseSnapshot> getCourseSnapshot(List<Long> courseIds) {

        // 강좌 조회
        Map<Long, CourseInternalResult> courses = courseInternalUseCase.getCoursesByIds(courseIds);

        // DTO 변환
        return courses.values().stream()
                .collect(Collectors.toMap(
                        CourseInternalResult::id,
                        course -> {
                            // 강좌의 강사 조회
                            String instructorName = userInternalUseCase.findById(course.instructorId())
                                    .map(UserInternalResult::nickName)
                                    .orElse(""); // 강사 정보가 없으면 빈 문자열 처리

                            return CourseSnapshot.builder()
                                    .courseId(course.id())
                                    .courseTitle(course.title())
                                    .courseStatus(course.courseStatus())
                                    .instructorName(instructorName)
                                    .thumbnailUrl(null) // FIXME: CourseInternalResult에 thumbnailResourceKey 필드 추가 후 매핑 필요
                                    .price(course.price())
                                    .build();
                        }
                ));
    }

    /*
     * 강좌 가격 조회
     */
    @Override
    public List<CoursePrice> getCoursePrices(List<Long> courseIds) {

        // 강좌 조회
        Map<Long, CourseInternalResult> courses = courseInternalUseCase.getCoursesByIds(courseIds);


        if (courses.size() != courseIds.size()) {
            // TODO: 정확히 어떤 courseId가 없는지도 전달할 필요성 고민!
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        // DTO 변환
        return courses.values().stream()
                .map(course -> new CoursePrice(course.id(), course.price()))
                .collect(Collectors.toList());
    }
}
