package com.lxp.aplus.enrollment.application.port.in;

import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentQueryPort {
    Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable);

    EnrollmentDetailResult getEnrollmentDetail(Long studentId, Long enrollmentId);

    EnrollmentDetailResult getEnrollmentDetailByCourseId(Long studentId, Long courseId);

    long getStudentCountForCourse(Long courseId);
}
