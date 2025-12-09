package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseQueryUseCase {
    private final CourseRepository courseRepository;
    private final CategoryQueryPort categoryQueryPort;

    public Page<CourseResponse> getInstructorCourses(Long instructorId, Pageable pageable) {
        Page<Course> courses = courseRepository.findAllByInstructorId(instructorId, pageable);
        return convertToCourseResponse(courses, pageable);
    }

    public Page<CourseResponse> getPublishedCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAllByPublished(pageable);
        return convertToCourseResponse(courses, pageable);
    }

    private Page<CourseResponse> convertToCourseResponse(Page<Course> courses, Pageable pageable) {
        List<CourseResponse> dtoList = courses.getContent().stream()
                .map(course -> {
                    List<String> categoryPath = getCategoryPath(course.getCategoryId());
                    return CourseResponse.of(course, categoryPath);
                })
                .toList();

        return new PageImpl<>(dtoList, pageable, courses.getTotalElements());
    }

    private List<String> getCategoryPath(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }
        return categoryQueryPort.findCategoryPathIds(categoryId);
    }

}
