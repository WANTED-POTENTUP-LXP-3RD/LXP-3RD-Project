package com.lxp.aplus.enrollment.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, expiredAt);

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        assertThat(enrollment.getProgressRate()).isEqualTo(0);
        assertThat(enrollment.getExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("of 정적 팩토리 메서드는 인자가 null이면 BusinessException을 던진다.")
    void of_fail_if_argument_is_null() {
        // given
        LocalDateTime expiredAt = LocalDateTime.now().plusYears(1);

        // when & then
        assertThatThrownBy(() -> Enrollment.of(null, COURSE_ID, expiredAt))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);

        assertThatThrownBy(() -> Enrollment.of(STUDENT_ID, null, expiredAt))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);

        assertThatThrownBy(() -> Enrollment.of(STUDENT_ID, COURSE_ID, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.INVALID_ARGUMENT);
    }

    @Test
    @DisplayName("진도율이 100%가 되면 자동으로 수료(COMPLETED) 상태로 변경된다.")
    void updateProgress_complete() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));

        // when
        enrollment.updateProgress(MAX_PROGRESS_RATE);

        // then
        assertThat(enrollment.getProgressRate()).isEqualTo(MAX_PROGRESS_RATE);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @DisplayName("유효하지 않은 진도율로 업데이트 시 BusinessException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void updateProgress_fail_if_invalid_rate(int invalidProgressRate) {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));

        // when & then
        assertThatThrownBy(() -> enrollment.updateProgress(invalidProgressRate))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.INVALID_PROGRESS_RATE);
    }

    @Test
    @DisplayName("수강 중이 아닌 강의의 진도율 업데이트 시 BusinessException을 던진다.")
    void updateProgress_fail_if_not_enrolled() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));
        enrollment.cancel();

        // when & then
        assertThatThrownBy(() -> enrollment.updateProgress(VALID_PROGRESS_RATE))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.CANNOT_UPDATE_PROGRESS_FOR_NON_ENROLLED);
    }

    @Test
    @DisplayName("만료된 강의의 진도율 업데이트 시 BusinessException을 던진다.")
    void updateProgress_fail_if_expired() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().minusDays(1));

        // when & then
        assertThatThrownBy(() -> enrollment.updateProgress(VALID_PROGRESS_RATE))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.ENROLLMENT_EXPIRED_PROGRESS_UPDATE_DENIED);
    }

    @Test
    @DisplayName("수강 신청을 취소하면 상태가 CANCELED로 변경된다.")
    void cancel() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));

        // when
        enrollment.cancel();

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }

    @Test
    @DisplayName("이미 수료한 강의는 취소할 수 없다.")
    void cancel_fail_if_completed() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));
        enrollment.updateProgress(MAX_PROGRESS_RATE);
        // when & then
        assertThatThrownBy(enrollment::cancel)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EnrollmentErrorCode.CANNOT_CANCEL_COMPLETED_ENROLLMENT);
    }

    @Test
    @DisplayName("이미 취소된 강의는 다시 취소할 수 없다.")
    void cancel_fail_if_already_cancelled() {
        // given
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().plusYears(1));
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
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, LocalDateTime.now().minusDays(1));

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
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, pastDate);

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
        Enrollment enrollment = Enrollment.of(STUDENT_ID, COURSE_ID, futureDate);

        // when
        boolean expired = enrollment.isExpired();

        // then
        assertThat(expired).isFalse();
    }
}