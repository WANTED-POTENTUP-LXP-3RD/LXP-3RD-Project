package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseInfo;
import com.lxp.aplus.enrollment.application.result.EnrollmentCreationResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnrollmentCommandUseCaseTest {

    @InjectMocks
    private EnrollmentCommandUseCase enrollmentCommandUseCase;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseFinder courseFinder;

    @Captor
    private ArgumentCaptor<Enrollment> enrollmentCaptor;

    private static final Long STUDENT_ID = 1L;
    private static final Long COURSE_ID_1 = 100L;
    private static final String IMP_UID = "imp_1234567890";
    private static final String MERCHANT_UID = "order_20251203_001";

    @Test
    @DisplayName("수강 신청이 정상적으로 완료되어야 한다.")
    void enroll_success() {
        // given
        EnrollmentCommand command = new EnrollmentCommand(IMP_UID, MERCHANT_UID, STUDENT_ID, COURSE_ID_1);
        Enrollment createdEnrollment = Enrollment.of(STUDENT_ID, COURSE_ID_1, LocalDateTime.now().plusYears(2));

        given(courseFinder.findCourseById(COURSE_ID_1)).willReturn(Optional.of(new CourseInfo(COURSE_ID_1, "Test Course")));
        given(enrollmentRepository.existsByStudentIdAndCourseId(STUDENT_ID, COURSE_ID_1)).willReturn(false);
        given(enrollmentRepository.save(any(Enrollment.class))).willReturn(createdEnrollment);

        // when
        EnrollmentCreationResult result = enrollmentCommandUseCase.enroll(command);

        // then
        verify(enrollmentRepository).save(enrollmentCaptor.capture());
        Enrollment capturedEnrollment = enrollmentCaptor.getValue();
        assertThat(capturedEnrollment.getStudentId()).isEqualTo(STUDENT_ID);
        assertThat(capturedEnrollment.getCourseId()).isEqualTo(COURSE_ID_1);

        assertThat(result).isNotNull();
        assertThat(result.totalCount()).isEqualTo(1);
        assertThat(result.enrollments()).hasSize(1);
        assertThat(result.enrollments().get(0).studentId()).isEqualTo(STUDENT_ID);
        assertThat(result.enrollments().get(0).courseId()).isEqualTo(COURSE_ID_1);
    }

    @Test
    @DisplayName("존재하지 않는 강의를 수강 신청하면 에러가 발생해야 한다.")
    void enroll_fail_course_not_found() {
        // given
        EnrollmentCommand command = new EnrollmentCommand(IMP_UID, MERCHANT_UID, STUDENT_ID, COURSE_ID_1);

        given(courseFinder.findCourseById(COURSE_ID_1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentCommandUseCase.enroll(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(EnrollmentErrorCode.ENROLLMENT_COURSE_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 수강 중인 강의라면 에러가 발생해야 한다.")
    void enroll_fail_duplicate() {
        // given
        EnrollmentCommand command = new EnrollmentCommand(IMP_UID, MERCHANT_UID, STUDENT_ID, COURSE_ID_1);

        given(courseFinder.findCourseById(COURSE_ID_1)).willReturn(Optional.of(new CourseInfo(COURSE_ID_1, "Test Course")));
        given(enrollmentRepository.existsByStudentIdAndCourseId(STUDENT_ID, COURSE_ID_1)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> enrollmentCommandUseCase.enroll(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(EnrollmentErrorCode.ALREADY_ENROLLED_COURSE);
    }
}
