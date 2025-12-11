package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.port.out.ProgressFinder;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressFinder progressFinder;

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

                    List<Long> lectureResourceIds = courseFinder.getLectureResourceIds(enrollment.getCourseId());
                    if (lectureResourceIds.isEmpty()) {
                        return EnrollmentListItemResult.of(
                                enrollment,
                                courseSummary,
                                0
                        );
                    }

                    Map<Long, Boolean> resourceIdToIsCompletedMap = progressFinder.getCompletionStatusMap(enrollment.getId(), lectureResourceIds);
                    long completedResources = resourceIdToIsCompletedMap.values()
                                                                        .stream()
                                                                        .filter(Boolean::booleanValue)
                                                                        .count();
                    int progressRate = (int) ((double) completedResources / lectureResourceIds.size() * 100);

                    return EnrollmentListItemResult.of(
                            enrollment,
                            courseSummary,
                            progressRate
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, enrollmentsPage.getTotalElements());
    }

    public EnrollmentDetailResult getEnrollmentDetail(Long studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        enrollment.validateOwner(studentId);

        List<Long> lectureResourceIds = courseFinder.getLectureResourceIds(enrollment.getCourseId());
        int progressRate = 0;
        if (!lectureResourceIds.isEmpty()) {
            Map<Long, Boolean> resourceIdToIsCompletedMap = progressFinder.getCompletionStatusMap(enrollment.getId(), lectureResourceIds);
            long completedResources = resourceIdToIsCompletedMap.values()
                                                                .stream()
                                                                .filter(Boolean::booleanValue)
                                                                .count();
            progressRate = (int) ((double) completedResources / lectureResourceIds.size() * 100);
        }

        return EnrollmentDetailResult.of(enrollment, progressRate);
    }



    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }
}
