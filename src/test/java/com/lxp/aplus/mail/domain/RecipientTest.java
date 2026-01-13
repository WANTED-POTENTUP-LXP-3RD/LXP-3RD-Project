package com.lxp.aplus.mail.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Recipient Value Object 테스트")
class RecipientTest {

    @Test
    @DisplayName("내부인(회원) 수신자를 생성할 수 있다")
    void internal_success() {
        // when
        Recipient recipient = Recipient.internal("user@example.com", 1L);

        // then
        assertThat(recipient.getEmail()).isEqualTo("user@example.com");
        assertThat(recipient.getUserId()).isPresent();
        assertThat(recipient.getUserId().get()).isEqualTo(1L);
        assertThat(recipient.isInternal()).isTrue();
        assertThat(recipient.isExternal()).isFalse();
    }

    @Test
    @DisplayName("외부인(비회원) 수신자를 생성할 수 있다")
    void external_success() {
        // when
        Recipient recipient = Recipient.external("external@example.com");

        // then
        assertThat(recipient.getEmail()).isEqualTo("external@example.com");
        assertThat(recipient.getUserId()).isEmpty();
        assertThat(recipient.isInternal()).isFalse();
        assertThat(recipient.isExternal()).isTrue();
    }

    @Test
    @DisplayName("이메일이 null이면 IllegalArgumentException을 던진다")
    void internal_fail_if_email_is_null() {
        // when & then
        assertThatThrownBy(() -> Recipient.internal(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 IllegalArgumentException을 던진다")
    void internal_fail_if_email_is_blank() {
        // when & then
        assertThatThrownBy(() -> Recipient.internal("   ", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("내부인인 경우 User ID가 null이면 IllegalArgumentException을 던진다")
    void internal_fail_if_userId_is_null() {
        // when & then
        assertThatThrownBy(() -> Recipient.internal("user@example.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("내부인인 경우 User ID는 필수입니다.");
    }

    @Test
    @DisplayName("외부인 생성 시 이메일이 null이면 IllegalArgumentException을 던진다")
    void external_fail_if_email_is_null() {
        // when & then
        assertThatThrownBy(() -> Recipient.external(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }

    @Test
    @DisplayName("외부인 생성 시 이메일이 빈 문자열이면 IllegalArgumentException을 던진다")
    void external_fail_if_email_is_blank() {
        // when & then
        assertThatThrownBy(() -> Recipient.external("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일 주소는 필수입니다.");
    }
}

