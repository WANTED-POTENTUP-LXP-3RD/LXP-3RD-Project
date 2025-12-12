package com.lxp.aplus.enrollment.application.port.out;

import java.util.List;
import java.util.Optional;

public interface CourseFinder {
    Optional<CourseSummary> findCourseById(Long courseId);
    int countLectures(Long courseId);
    List<Long> getLectureResourceIds(Long courseId);
    List<LectureResourceSummary> findAllLectureResourcesByCourseId(Long courseId);
}