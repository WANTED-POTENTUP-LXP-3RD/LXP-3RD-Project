package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.review.application.port.out.CourseQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseQueryPortAdapter implements CourseQueryPort {
    private final CourseRepository courseRepository;

    @Override
    public Optional<Course> findCourse(long courseId) {
        return courseRepository.findById(courseId);
    }
}
