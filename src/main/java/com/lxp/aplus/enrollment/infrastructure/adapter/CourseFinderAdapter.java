package com.lxp.aplus.enrollment.infrastructure.adapter;

import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseInfo;


import com.lxp.aplus.course.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseFinderAdapter implements CourseFinder {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseInfo> findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .map(course -> new CourseInfo(course.getId()));
    }
}