package com.lxp.aplus.enrollment.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class EnrollmentTest {

    private static final Long STUDENT_ID = 1L;
    private static final Long COURSE_ID = 100L;
    private static final int MAX_PROGRESS_RATE = 100;
    private static final int VALID_PROGRESS_RATE = 50;


    @Test
    @DisplayName("수강 신청 시 초기 상태는 ENROLLED이며 진도율은 0이다.")
    void enroll() {
        // given
        LocalDateTime expiredAt = LocalDateTime.now().plusYears(1);

        // when
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
    }

    @Test
    @DisplayName("of 정적 팩토리 메서드는 인자가 null이면 BusinessException을 던진다.")
    void of_fail_if_argument_is_null() {
        // given
        LocalDateTime expiredAt = LocalDateTime.now().plusYears(1);

        // when & then
        assertThatThrownBy(() -> Enrollment.of(null, COURSE_ID, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);

        assertThatThrownBy(() -> Enrollment.of(STUDENT_ID, null, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);
    }

    @Test
    @DisplayName("수강 신청을 취소하면 상태가 CANCELED로 변경된다.")
    void cancel() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);

        // when
        enrollment.cancel();

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }

    @Test
    @DisplayName("이미 취소된 강의는 다시 취소할 수 없다.")
    void cancel_fail_if_already_cancelled() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);
        enrollment.cancel();

        // when & then
        assertThatThrownBy(enrollment::cancel)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.ALREADY_CANCELLED_ENROLLMENT);
    }

    @Test
    @DisplayName("만료된 강의는 취소할 수 없다.")
    void cancel_fail_if_expired() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);
        ReflectionTestUtils.setField(enrollment, "expiredAt", LocalDateTime.now().minusDays(1));

        // when & then
        assertThatThrownBy(enrollment::cancel)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.CANNOT_CANCEL_EXPIRED_ENROLLMENT);
    }


    @Test
    @DisplayName("수강 기간이 지났는지 확인한다.")
    void isExpired() {
        // given: 어제 날짜로 만료일 설정
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);
        ReflectionTestUtils.setField(enrollment, "expiredAt", LocalDateTime.now().minusDays(1));

        // when
        boolean expired = enrollment.isExpired();

        // then
        assertThat(expired).isTrue();
    }

    @Test
    @DisplayName("수강 기간이 지나지 않았는지 확인한다.")
    void isNotExpired() {
        // given: 내일 날짜로 만료일 설정
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, 1L);

        // when
        boolean expired = enrollment.isExpired();

        // then
        assertThat(expired).isFalse();
    }
}