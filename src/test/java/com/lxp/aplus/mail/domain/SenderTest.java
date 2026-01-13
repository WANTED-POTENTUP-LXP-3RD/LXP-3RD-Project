package com.lxp.aplus.mail.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Sender Value Object 테스트")
class SenderTest {

    @Test
    @DisplayName("내부인 발신자를 생성할 수 있다")
    void internal_success() {
        // when
        Sender sender = Sender.internal("sender@example.com", 1L);

        // then
        assertThat(sender.getEmail()).isEqualTo("sender@example.com");
        assertThat(sender.getUserId()).isPresent();
        assertThat(sender.getUserId().get()).isEqualTo(1L);
        assertThat(sender.isInternal()).isTrue();
        assertThat(sender.isExternal()).isFalse();
    }

    @Test
    @DisplayName("시스템 발신자를 생성할 수 있다")
    void system_success() {
        // when
        Sender sender = Sender.system("noreply@example.com");

        // then
        assertThat(sender.getEmail()).isEqualTo("noreply@example.com");
        assertThat(sender.getUserId()).isEmpty();
        assertThat(sender.isInternal()).isFalse();
        assertThat(sender.isExternal()).isTrue();
    }

    @Test
    @DisplayName("이메일이 null이면 IllegalArgumentException을 던진다")
    void internal_fail_if_email_is_null() {
        // when & then
        assertThatThrownBy(() -> Sender.internal(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 IllegalArgumentException을 던진다")
    void internal_fail_if_email_is_blank() {
        // when & then
        assertThatThrownBy(() -> Sender.internal("   ", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("내부인인 경우 User ID가 null이면 IllegalArgumentException을 던진다")
    void internal_fail_if_userId_is_null() {
        // when & then
        assertThatThrownBy(() -> Sender.internal("sender@example.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("내부인인 경우 User ID는 필수입니다.");
    }

    @Test
    @DisplayName("시스템 발신자 생성 시 이메일이 null이면 IllegalArgumentException을 던진다")
    void system_fail_if_email_is_null() {
        // when & then
        assertThatThrownBy(() -> Sender.system(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("시스템 발신자 생성 시 이메일이 빈 문자열이면 IllegalArgumentException을 던진다")
    void system_fail_if_email_is_blank() {
        // when & then
        assertThatThrownBy(() -> Sender.system("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }
}

