package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentListQueryResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;

    public EnrollmentListQueryResult getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

                    return EnrollmentListItemResult.of(enrollment, courseSummary);
                })
                .collect(Collectors.toList());

        return EnrollmentListQueryResult.of(enrollmentsPage, content);
    }
}
