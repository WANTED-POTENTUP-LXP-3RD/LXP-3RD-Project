package com.lxp.aplus.enrollment.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.in.EnrollmentQueryUseCase;
import com.lxp.aplus.enrollment.application.port.out.CourseQueryPort;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.port.out.ProgressQueryPort;
import com.lxp.aplus.enrollment.application.port.out.dto.EnrollmentProgressDto;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.application.port.out.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentQueryService implements EnrollmentQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseQueryPort courseQueryPort;
    private final ProgressQueryPort progressQueryPort;

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        List<EnrollmentStatus> statuses = resolveStatuses(status);
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatusIn(studentId, statuses, pageable);

        // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseQueryPort에 findCoursesByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseQueryPort.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                    
                    List<String> categoryNames = courseQueryPort.findCategoryNamesByCourseId(enrollment.getCourseId());

                    EnrollmentProgressDto progress = progressQueryPort.getProgress(enrollment.getId(), enrollment.getCourseId());
                    int overallProgressRate = enrollment.getStatus() == EnrollmentStatus.COMPLETED ? 100 : progress.overallProgressRate();

                    return EnrollmentListItemResult.of(
                            enrollment,
                            courseSummary,
                            overallProgressRate,
                            categoryNames
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, enrollmentsPage.getTotalElements());
    }

    public EnrollmentDetailResult getEnrollmentDetail(Long studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        enrollment.validateOwner(studentId);

        EnrollmentProgressDto progress = progressQueryPort.getProgress(enrollment.getId(), enrollment.getCourseId());
        int overallProgressRate = enrollment.getStatus() == EnrollmentStatus.COMPLETED ? 100 : progress.overallProgressRate();

        return EnrollmentDetailResult.of(enrollment, overallProgressRate);
    }

    public EnrollmentDetailResult getEnrollmentDetailByCourseId(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        EnrollmentProgressDto progress = progressQueryPort.getProgress(enrollment.getId(), enrollment.getCourseId());
        int overallProgressRate = enrollment.getStatus() == EnrollmentStatus.COMPLETED ? 100 : progress.overallProgressRate();

        return EnrollmentDetailResult.of(enrollment, overallProgressRate);
    }

    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    private List<EnrollmentStatus> resolveStatuses(EnrollmentStatus status) {
        if (status != null) {
            return List.of(status);
        }
        return Arrays.stream(EnrollmentStatus.values())
                .filter(EnrollmentStatus::isActive)
                .toList();
    }

}
