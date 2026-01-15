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

    @Test
    @DisplayName("수강 신청 시 초기 상태는 ENROLLED이다.")
    void enroll_success() {
        // when
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
    }

    @Test
    @DisplayName("create 정적 팩토리 메서드는 필수 인자가 null이면 BusinessException을 던진다.")
    void create_fail_if_argument_is_null() {
        // when & then
        assertThatThrownBy(() -> Enrollment.create(null, COURSE_ID, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);

        assertThatThrownBy(() -> Enrollment.create(STUDENT_ID, null, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);
    }

    @Test
    @DisplayName("수강 신청을 취소하면 상태가 CANCELED로 변경된다.")
    void cancel_success() {
        // given
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);

        // when
        enrollment.cancel(LocalDateTime.now());

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }

    @Test
    @DisplayName("이미 취소된 강의는 다시 취소할 수 없다.")
    void cancel_fail_if_already_cancelled() {
        // given
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);
        enrollment.cancel(LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> enrollment.cancel(LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.ENROLLMENT_ALREADY_CANCELLED);
    }

    @Test
    @DisplayName("만료된 강의는 취소할 수 없다.")
    void cancel_fail_if_expired() {
        // given
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);
        ReflectionTestUtils.setField(enrollment, "expiredAt", LocalDateTime.now().minusDays(1));

        // when & then
        assertThatThrownBy(() -> enrollment.cancel(LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.ENROLLMENT_CANNOT_CANCEL_EXPIRED);
    }

    @Test
    @DisplayName("수강 기간이 지났는지 확인한다.")
    void isExpired_true() {
        // given: 어제 날짜로 만료일 설정
        LocalDateTime currentTime = LocalDateTime.now();
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);
        ReflectionTestUtils.setField(enrollment, "expiredAt", currentTime.minusDays(1));

        // when
        boolean expired = enrollment.isExpired(currentTime);

        // then
        assertThat(expired).isTrue();
    }

    @Test
    @DisplayName("수강 기간이 지나지 않았는지 확인한다.")
    void isExpired_false() {
        // given: 내일 날짜로 만료일 설정
        LocalDateTime currentTime = LocalDateTime.now();
        Enrollment enrollment = Enrollment.create(STUDENT_ID, COURSE_ID, 1L);
        ReflectionTestUtils.setField(enrollment, "expiredAt", currentTime.plusDays(1));

        // when
        boolean expired = enrollment.isExpired(currentTime);

        // then
        assertThat(expired).isFalse();
    }
}