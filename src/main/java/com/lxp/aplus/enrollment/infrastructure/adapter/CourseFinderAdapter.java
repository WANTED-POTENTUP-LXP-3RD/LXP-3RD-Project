package com.lxp.aplus.enrollment.infrastructure.adapter;

import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;


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
    public Optional<CourseSummary> findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .map(course -> new CourseSummary(course.getId(), course.getTitle()));
    }

    @Override
    public int countLectures(Long courseId) {
        return courseRepository.countLecturesByCourseId(courseId);
    }
}