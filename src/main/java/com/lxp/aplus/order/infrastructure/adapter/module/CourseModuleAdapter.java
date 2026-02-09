package com.lxp.aplus.order.infrastructure.adapter.module;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.internal.dto.CourseInternalResult;
import com.lxp.aplus.course.application.internal.usecase.CourseInternalUseCase;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.application.port.out.CourseSalesStatus;
import com.lxp.aplus.order.application.port.out.CourseSnapshot;
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

    CourseInternalUseCase courseInternalUseCase;
    UserInternalUseCase userInternalUseCase;

    @Override
    public boolean isCoursePublished(Long courseId) {

        return courseInternalUseCase.findById(courseId)
                .map(result -> result.courseStatus() == CourseStatus.PUBLISHED)
                .orElse(false);
    }

    @Override
    public Map<Long, CourseSalesStatus> getCourseSalesStatusByIds(List<Long> courseIds) {

        Map<Long, CourseInternalResult> courses = courseInternalUseCase.getCoursesByIds(courseIds);

        if (courses.size() != courseIds.size()) {
            throw new BusinessException(CourseErrorCode.COURSE_NOT_FOUND);
        }

        return courses.values().stream()
                .collect(Collectors.toMap(
                        CourseInternalResult::id,
                        course -> new CourseSalesStatus(course.price(), course.courseStatus())
                ));
    }

    @Override
    public Map<Long, CourseSnapshot> getCourseSnapshot(List<Long> courseIds) {

        // 1. 강좌 정보 조회
        Map<Long, CourseInternalResult> courseMap = courseInternalUseCase.getCoursesByIds(courseIds);

        // 4. 데이터 조합 및 매핑
        return courseMap.values().stream()
                .collect(Collectors.toMap(
                        CourseInternalResult::id,
                        course -> {
                            // UserInternalUseCase를 호출하여 강사 이름 조회 (Optional 처리)
                            String instructorName = userInternalUseCase.findById(course.instructorId())
                                    .map(UserInternalResult::nickName) // UserInternalResult에 name() 메서드가 있다고 가정
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
}
