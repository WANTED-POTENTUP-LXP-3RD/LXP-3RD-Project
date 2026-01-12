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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressFinder progressFinder;

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseFinder에 findCourseByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                    
                    List<String> categoryNames = courseFinder.findCategoryNamesByCourseId(enrollment.getCourseId());

                    int completionPercentage = calculateCompletionPercentage(enrollment);

                    return EnrollmentListItemResult.of(
                            enrollment,
                            courseSummary,
                            completionPercentage,
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

        int completionPercentage = calculateCompletionPercentage(enrollment);

        return EnrollmentDetailResult.of(enrollment, completionPercentage);
    }

    public EnrollmentDetailResult getEnrollmentDetailByCourseId(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        int completionPercentage = calculateCompletionPercentage(enrollment);

        return EnrollmentDetailResult.of(enrollment, completionPercentage);
    }

    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    /**
     * 강의 리소스 수와 완료된 리소스 수를 기반으로 진도율을 계산하는 헬퍼 메서드입니다.
     * 실제 계산 로직은 Enrollment 도메인 객체에 위임합니다.
     *
     * @param enrollment 진도율을 계산할 수강 정보 엔티티
     * @return 계산된 진도율 (0-100)
     */
    private int calculateCompletionPercentage(Enrollment enrollment) {
        List<Long> lectureResourceIds = courseFinder.getLectureResourceIds(enrollment.getCourseId());
        if (lectureResourceIds.isEmpty()) {
            return 0;
        }

        Map<Long, Boolean> resourceIdToIsCompletedMap = progressFinder.getCompletionStatusMap(enrollment.getId(), lectureResourceIds);
        long completedResourceCount = resourceIdToIsCompletedMap.values()
                .stream()
                .filter(Boolean::booleanValue)
                .count();

        return enrollment.calculateProgressRate(completedResourceCount, lectureResourceIds.size());
    }
}


