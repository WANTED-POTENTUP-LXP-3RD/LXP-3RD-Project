package com.lxp.aplus.review.application.port.out;

import com.lxp.aplus.course.domain.Course;

import java.util.Optional;

public interface CourseFinder {
    Optional<Course> findCourse(long courseId);
}
