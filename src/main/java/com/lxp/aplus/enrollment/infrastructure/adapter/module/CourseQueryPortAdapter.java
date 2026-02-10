package com.lxp.aplus.enrollment.infrastructure.adapter.module;

import com.lxp.aplus.category.application.internal.usecase.CategoryInternalUseCase;
import com.lxp.aplus.course.application.internal.usecase.CourseInternalUseCase;
import com.lxp.aplus.enrollment.application.port.out.CourseQueryPort;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Component("enrollmentCourseQueryPortAdapter")
@RequiredArgsConstructor
public class CourseQueryPortAdapter implements CourseQueryPort {

    private final CourseInternalUseCase courseInternalUseCase;
    private final CategoryInternalUseCase categoryInternalUseCase;

    @Override
    @Transactional(readOnly = true)
    // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseQueryPort에 findCoursesByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
    public Optional<CourseSummary> findCourseById(Long courseId) {

        return courseInternalUseCase.findById(courseId)
                .map(course -> new CourseSummary(course.id(), course.title()));
    }

    @Override
    @Transactional(readOnly = true)
    // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseQueryPort에 findCategoryNamesByCourseIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
    public List<String> findCategoryNamesByCourseId(Long courseId) {
        return courseInternalUseCase.findById(courseId)
                .map(course -> course.categoryId())
                .filter(categoryId -> categoryId != null)
                .flatMap(categoryInternalUseCase::findByIdWithParent)
                .map(category -> {
                    if (category.parentName() == null) {
                        return List.of(category.name());
                    }
                    return List.of(category.parentName(), category.name());
                })
                .orElse(Collections.emptyList());
    }
}
