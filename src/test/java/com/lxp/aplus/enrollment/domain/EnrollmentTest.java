package com.lxp.aplus.enrollment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class EnrollmentTest {

    @Test
    @DisplayName("수강 신청 시 초기 상태는 ENROLLED이며 진도율은 0이다.")
    void enroll() {
        // given
        Long studentId = 1L;
        Long courseId = 100L;
        LocalDateTime expiredAt = LocalDateTime.now().plusYears(1);

        // when
        Enrollment enrollment = Enrollment.enroll(studentId, courseId, expiredAt);

        // then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        assertThat(enrollment.getProgressRate()).isEqualTo(0);
        assertThat(enrollment.getExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("진도율이 100%가 되면 자동으로 수료(COMPLETED) 상태로 변경된다.")
    void updateProgress_complete() {
        // given
        Enrollment enrollment = Enrollment.enroll(1L, 100L, LocalDateTime.now().plusYears(1));

        // when
        enrollment.updateProgress(100);

        // then
        assertThat(enrollment.getProgressRate()).isEqualTo(100);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("이미 수료한 강의는 취소할 수 없다.")
    void cancel_fail_if_completed() {
        // given
        Enrollment enrollment = Enrollment.enroll(1L, 100L, LocalDateTime.now().plusYears(1));
        enrollment.complete();

        // when & then
        assertThatThrownBy(enrollment::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 수료한 강의는 취소할 수 없습니다.");
    }

    @Test
    @DisplayName("수강 기간이 지났는지 확인한다.")
    void isExpired() {
        // given: 어제 날짜로 만료일 설정
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        Enrollment enrollment = Enrollment.enroll(1L, 100L, pastDate);

        // when
        boolean expired = enrollment.isExpired();

        // then
        assertThat(expired).isTrue();
    }
}