package com.lxp.aplus.course.application.port.out;

public interface EnrollmentQueryPort {
    int getStudentCount(Long courseId);

    boolean isEnrolled(Long userId, Long courseId);
}
