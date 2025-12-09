package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.result.EnrollmentCreationResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentCommandUseCase{

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;

    /**
     * 수강 신청 비즈니스 로직을 수행합니다.
     * * <p> 처리 흐름:
     * 1. [중복 검사] 이미 수강 중인 강의인지 확인 (중복 시 예외 발생)
     * 2. [엔티티 생성] 수강 기간(2년) 정책을 적용하여 Enrollment 엔티티 생성
     * 3. [저장] 생성된 수강 내역 저장 및 결과 반환
     * </p>
     *
     * @param command 수강 신청 요청 데이터 (studentId, courseId, impUid 등)
     * @return EnrollmentCreationResult 생성된 수강 내역 정보
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnrollmentCreationResult enroll(EnrollmentCommand command) {
        Long courseId = command.courseId();

        courseFinder.findCourseById(courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_COURSE_NOT_FOUND));

        if (enrollmentRepository.existsByStudentIdAndCourseId(command.studentId(), courseId)) {
            throw new BusinessException(EnrollmentErrorCode.ALREADY_ENROLLED_COURSE);
        }

        LocalDateTime expiredAt = LocalDateTime.now().plusYears(2);

        Enrollment enrollmentToSave = Enrollment.of(command.studentId(), courseId, expiredAt);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollmentToSave);

        EnrollmentDetailResult createdEnrollment = EnrollmentDetailResult.from(savedEnrollment);

        return EnrollmentCreationResult.from(List.of(createdEnrollment));
    }
}
