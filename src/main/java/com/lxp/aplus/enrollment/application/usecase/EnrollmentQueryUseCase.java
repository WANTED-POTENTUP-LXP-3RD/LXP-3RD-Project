package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.progress.application.port.in.ProgressQueryPort;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
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
public class EnrollmentQueryUseCase implements ProgressQueryPort {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressRepository progressRepository;

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

                    int totalLectures = courseFinder.countLectures(enrollment.getCourseId());
                    if (totalLectures == 0) {
                        return EnrollmentListItemResult.of(enrollment, courseSummary, 0);
                    }

                    long completedLectures = progressRepository.countByEnrollmentIdAndIsCompleted(enrollment.getId(), true);
                    int progressRate = (int) (((double) completedLectures / totalLectures) * 100);

                    return EnrollmentListItemResult.of(enrollment, courseSummary, progressRate);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, enrollmentsPage.getTotalElements());
    }

    @Override
    public Map<Long, Boolean> checkLectureCompletionStatus(Long enrollmentId, List<Long> lectureResourceIds) {
        List<Progress> progresses = progressRepository.findByEnrollmentIdAndLectureResourceIds(enrollmentId, lectureResourceIds);

        return progresses.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getLectureResource().getId(),
                        Progress::isCompleted
                ));
    }

    public boolean isEnrollmentCompleted(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));
        return enrollment.getStatus() == EnrollmentStatus.COMPLETED;
    }

    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }
}
