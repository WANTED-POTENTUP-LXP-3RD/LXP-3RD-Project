package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.port.out.ProgressReader;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressReader progressReader; // ProgressFinder -> ProgressReader로 변경

    public Page<EnrollmentListItemResult> getEnrollmentList(Long studentId, EnrollmentStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage = enrollmentRepository.findByStudentIdAndStatus(studentId, status, pageable);

        // TODO: [성능 개선] N+1 쿼리 발생 지점. CourseFinder에 findCourseByIds(List<Long> courseIds)와 같은 배치 조회 기능 추가 필요.
        List<EnrollmentListItemResult> content = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    CourseSummary courseSummary = courseFinder.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                    
                    List<String> categoryNames = courseFinder.findCategoryNamesByCourseId(enrollment.getCourseId());

                    // 기존 계산 로직 제거, Progress 모듈에 조회
                    int overallProgressRate = progressReader.getOverallProgressRate(enrollment.getId(), enrollment.getCourseId());

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

        // 기존 계산 로직 제거, Progress 모듈에 조회
        int overallProgressRate = progressReader.getOverallProgressRate(enrollment.getId(), enrollment.getCourseId());

        return EnrollmentDetailResult.of(enrollment, overallProgressRate);
    }

    public EnrollmentDetailResult getEnrollmentDetailByCourseId(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        // 기존 계산 로직 제거, Progress 모듈에 조회
        int overallProgressRate = progressReader.getOverallProgressRate(enrollment.getId(), enrollment.getCourseId());

        return EnrollmentDetailResult.of(enrollment, overallProgressRate);
    }

    public long getStudentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }
}


