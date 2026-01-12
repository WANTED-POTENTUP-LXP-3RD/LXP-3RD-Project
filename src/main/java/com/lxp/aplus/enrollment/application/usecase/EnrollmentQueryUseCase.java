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

    private static final int DEFAULT_PROGRESS_RATE_WHEN_NO_RESOURCES = 0;

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                    
                    // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseFinder에 findCourseByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
                    List<String> categoryNames = courseFinder.findCategoryNamesByCourseId(enrollment.getCourseId());

                    int completionPercentage = calculateCompletionPercentage(enrollment.getId(), enrollment.getCourseId(), categoryNames);

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

        List<String> categoryNames = courseFinder.findCategoryNamesByCourseId(enrollment.getCourseId());
        int completionPercentage = calculateCompletionPercentage(enrollment.getId(), enrollment.getCourseId(), categoryNames);

        return EnrollmentDetailResult.of(enrollment, completionPercentage);
    }

    public EnrollmentDetailResult getEnrollmentDetailByCourseId(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        List<String> categoryNames = courseFinder.findCategoryNamesByCourseId(enrollment.getCourseId());
        int completionPercentage = calculateCompletionPercentage(enrollment.getId(), enrollment.getCourseId(), categoryNames);

        return EnrollmentDetailResult.of(enrollment, completionPercentage);
    }

    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    /**
     * 강의 리소스 수와 완료된 리소스 수를 기반으로 진도율을 계산하는 헬퍼 메서드.
     *
     * @param enrollmentId       수강 ID
     * @param courseId           강의 ID
     * @param categoryNames      강의 카테고리 이름 목록 (진도율 계산에 직접 사용되지는 않지만, 인자를 받아서 UseCase 로직을 단순화하는 역할)
     * @return 계산된 진도율 (0-100)
     */
    private int calculateCompletionPercentage(Long enrollmentId, Long courseId, List<String> categoryNames) {
        List<Long> lectureResourceIds = courseFinder.getLectureResourceIds(courseId);
        if (lectureResourceIds.isEmpty()) {
            return DEFAULT_PROGRESS_RATE_WHEN_NO_RESOURCES;
        }

        long totalResourceCount = lectureResourceIds.size();
        Map<Long, Boolean> resourceIdToIsCompletedMap = progressFinder.getCompletionStatusMap(enrollmentId, lectureResourceIds);
        long completedResourceCount = resourceIdToIsCompletedMap.values()
                .stream()
                .filter(Boolean::booleanValue)
                .count();

        return (int) ((double) completedResourceCount / totalResourceCount * 100);
    }
}


