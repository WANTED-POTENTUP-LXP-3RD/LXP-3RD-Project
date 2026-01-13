package com.lxp.aplus.mail.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Mail 도메인 테스트")
class MailTest {

    @Test
    @DisplayName("Mail을 생성할 수 있다")
    void create_success() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        String subject = "테스트 제목";
        String content = "<html>테스트 내용</html>";

        // when
        Mail mail = Mail.create(recipient, sender, subject, content);

        // then
        assertThat(mail.getRecipient()).isEqualTo(recipient);
        assertThat(mail.getSender()).isEqualTo(sender);
        assertThat(mail.getSubject()).isEqualTo(subject);
        assertThat(mail.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("수신자 이메일 주소를 조회할 수 있다")
    void getRecipientEmail_success() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        Mail mail = Mail.create(recipient, sender, "제목", "내용");

        // when
        String recipientEmail = mail.getRecipientEmail();

        // then
        assertThat(recipientEmail).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("발신자 이메일 주소를 조회할 수 있다")
    void getSenderEmail_success() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        Mail mail = Mail.create(recipient, sender, "제목", "내용");

        // when
        String senderEmail = mail.getSenderEmail();

        // then
        assertThat(senderEmail).isEqualTo("noreply@example.com");
    }

    @Test
    @DisplayName("수신자가 내부인인지 확인할 수 있다")
    void isRecipientInternal_success() {
        // given
        Recipient internalRecipient = Recipient.internal("user@example.com", 1L);
        Recipient externalRecipient = Recipient.external("external@example.com");
        Sender sender = Sender.system("noreply@example.com");

        // when
        Mail internalMail = Mail.create(internalRecipient, sender, "제목", "내용");
        Mail externalMail = Mail.create(externalRecipient, sender, "제목", "내용");

        // then
        assertThat(internalMail.isRecipientInternal()).isTrue();
        assertThat(externalMail.isRecipientInternal()).isFalse();
    }

    @Test
    @DisplayName("수신자가 외부인인지 확인할 수 있다")
    void isRecipientExternal_success() {
        // given
        Recipient internalRecipient = Recipient.internal("user@example.com", 1L);
        Recipient externalRecipient = Recipient.external("external@example.com");
        Sender sender = Sender.system("noreply@example.com");

        // when
        Mail internalMail = Mail.create(internalRecipient, sender, "제목", "내용");
        Mail externalMail = Mail.create(externalRecipient, sender, "제목", "내용");

        // then
        assertThat(internalMail.isRecipientExternal()).isFalse();
        assertThat(externalMail.isRecipientExternal()).isTrue();
    }

    @Test
    @DisplayName("수신자 User ID를 조회할 수 있다 (내부인인 경우)")
    void getRecipientUserId_success() {
        // given
        Recipient internalRecipient = Recipient.internal("user@example.com", 1L);
        Recipient externalRecipient = Recipient.external("external@example.com");
        Sender sender = Sender.system("noreply@example.com");

        // when
        Mail internalMail = Mail.create(internalRecipient, sender, "제목", "내용");
        Mail externalMail = Mail.create(externalRecipient, sender, "제목", "내용");

        // then
        assertThat(internalMail.getRecipientUserId()).isPresent();
        assertThat(internalMail.getRecipientUserId().get()).isEqualTo(1L);
        assertThat(externalMail.getRecipientUserId()).isEmpty();
    }

    @Test
    @DisplayName("발신자가 내부인인지 확인할 수 있다")
    void isSenderInternal_success() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender internalSender = Sender.internal("sender@example.com", 2L);
        Sender systemSender = Sender.system("noreply@example.com");

        // when
        Mail internalMail = Mail.create(recipient, internalSender, "제목", "내용");
        Mail systemMail = Mail.create(recipient, systemSender, "제목", "내용");

        // then
        assertThat(internalMail.isSenderInternal()).isTrue();
        assertThat(systemMail.isSenderInternal()).isFalse();
    }

    @Test
    @DisplayName("발신자가 시스템 발신자인지 확인할 수 있다")
    void isSenderSystem_success() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender internalSender = Sender.internal("sender@example.com", 2L);
        Sender systemSender = Sender.system("noreply@example.com");

        // when
        Mail internalMail = Mail.create(recipient, internalSender, "제목", "내용");
        Mail systemMail = Mail.create(recipient, systemSender, "제목", "내용");

        // then
        assertThat(internalMail.isSenderSystem()).isFalse();
        assertThat(systemMail.isSenderSystem()).isTrue();
    }
}

