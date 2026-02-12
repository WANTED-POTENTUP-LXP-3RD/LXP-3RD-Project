package com.lxp.aplus.enrollment.application.port.out;

import java.util.List;
import java.util.Optional;

public interface CourseQueryPort {
    Optional<CourseSummary> findCourseById(Long courseId);

    List<String> findCategoryNamesByCourseId(Long courseId);
}
