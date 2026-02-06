package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.event.EnrollmentCanceledEvent;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.ProgressReader;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentCommandUseCase{

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressReader progressReader; // ProgressFinder -> ProgressReader로 변경
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 수강 신청 비즈니스 로직을 수행.
     * *처리 흐름:
     * 1. [중복 검사] 이미 수강 중인 강의인지 확인 (중복 시 예외 발생)
     * 2. [엔티티 생성] 수강 기간(2년) 정책을 적용하여 Enrollment 엔티티 생성
     * 3. [저장] 생성된 수강 내역 저장 및 결과 반환
     *
     * @param command 수강 신청 요청 데이터 (studentId, courseId, orderItemId 등)
     * @return Long 생성된 수강 내역 ID
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long enroll(EnrollmentCommand command) {
        Long courseId = command.courseId();

        courseFinder.findCourseById(courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_COURSE_NOT_FOUND));

        if (enrollmentRepository.existsByStudentIdAndCourseId(command.studentId(), courseId)) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_ALREADY_ENROLLED);
        }

        Enrollment enrollmentToSave = Enrollment.create(command.studentId(), courseId, command.orderItemId());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollmentToSave);

        return savedEnrollment.getId();
    }

    /**
     * 수강 취소 비즈니스 로직을 수행합니다.
     * 처리 흐름:
     * 1. [조회 및 권한 검증] 취소할 수강 내역을 조회하고, 요청한 사용자가 소유자인지 확인합니다.
     * 2. [학습 이력 검증] 이미 학습을 시작했는지 확인합니다. (시작했다면 예외 발생)
     * 3. [상태 변경] 수강 내역의 상태를 'CANCELED'로 변경합니다.
     * 4. [이벤트 발행] 수강 취소 이벤트를 발행하여 후속 처리(환불 등)를 위임합니다.
     * 5. [데이터 정리] 관련된 모든 학습 이력을 삭제하여 데이터 정합성을 유지합니다.
     *
     *
     * @param enrollmentId 취소할 수강 ID
     * @param studentId 요청한 학생 ID
     * @return Enrollment 취소 처리된 수강 정보
     */
    @Transactional
    public Enrollment cancel(Long enrollmentId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_TO_CANCEL_NOT_FOUND));

        enrollment.validateOwner(studentId);

        if (progressReader.hasProgress(enrollmentId)) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_CANNOT_CANCEL_AFTER_STARTED);
        }

        enrollment.cancel(LocalDateTime.now());

        progressReader.removeByEnrollmentId(enrollmentId);

        eventPublisher.publishEvent(new EnrollmentCanceledEvent(enrollment.getOrderItemId()));

        return enrollment;
    }
}
