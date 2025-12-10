package com.lxp.aplus.enrollment.application.port.out;

import java.util.Optional;

public interface CourseFinder {
    Optional<CourseSummary> findCourseById(Long courseId);
}