package com.lxp.aplus.course.application.port.out;

import java.util.List;
import java.util.Map;

public interface EnrollmentQueryPort {
    int getStudentCount(Long courseId);
    boolean isEnrolled(Long userId, Long courseId);
    Map<Long, Integer> getStudentCountBatch(List<Long> courseIds);
}
