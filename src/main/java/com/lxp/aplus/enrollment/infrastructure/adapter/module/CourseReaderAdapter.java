package com.lxp.aplus.enrollment.infrastructure.adapter.module;

import com.lxp.aplus.category.application.internal.usecase.CategoryInternalUseCase;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.application.port.out.CourseReader;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class CourseReaderAdapter implements CourseReader {

    private final CourseRepository courseRepository;
    private final CategoryInternalUseCase categoryInternalUseCase;

    @Override
    @Transactional(readOnly = true)
    // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseReader에 findCoursesByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
    public Optional<CourseSummary> findCourseById(Long courseId) {

        return courseRepository.findById(courseId)
                .map(course -> new CourseSummary(course.getId(), course.getTitle()));
    }

    @Override
    @Transactional(readOnly = true)
    // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseReader에 findCategoryNamesByCourseIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
    public List<String> findCategoryNamesByCourseId(Long courseId) {
        return courseRepository.findById(courseId)
                .flatMap(course -> categoryInternalUseCase.findByIdWithParent(course.getCategoryId()))
                .map(category -> {
                    return buildCategoryNames(category.name(), category.parent());
                })
                .orElse(Collections.emptyList());
    }

    private List<String> buildCategoryNames(String leafName, com.lxp.aplus.category.domain.Category parent) {
        List<String> names = new java.util.ArrayList<>();
        names.add(leafName);
        var current = parent;
        while (current != null) {
            names.add(0, current.getName());
            current = current.getParent();
        }
        return names;
    }
}
