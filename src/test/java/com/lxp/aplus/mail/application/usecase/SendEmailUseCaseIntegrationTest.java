package com.lxp.aplus.mail.application.usecase;

import com.lxp.aplus.mail.domain.Recipient;
import com.lxp.aplus.mail.domain.Sender;
import com.lxp.aplus.mail.domain.template.MailTemplateCode;
import com.lxp.aplus.mail.domain.template.variable.EmailVerificationVariable;
import com.lxp.aplus.mail.domain.template.variable.WelcomeMailVariable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 메일 발송 통합 테스트
 * 
 * 실제 Gmail SMTP를 통해 메일을 발송하는 통합 테스트.
 * 
 * 주의사항:
 * - 실제 메일이 발송되므로 테스트용 이메일 주소 사용필요.
 * - Gmail SMTP 설정이 필요 (application-local.yml).
 * 
 * 이메일 주소 설정 방법 (우선순위):
 * 1. 환경변수: TEST_RECIPIENT_EMAIL, TEST_SENDER_EMAIL (가장 우선)
 * 2. 테스트 케이스 상수: 아래 TEST_RECIPIENT_EMAIL, TEST_SENDER_EMAIL 직접 수정
 * 
 * 실행 방법:
 * 1. application-local.yml에 Gmail 계정 정보 설정
 * 2. 환경변수 설정 또는 테스트 케이스의 이메일 주소를 실제 주소로 변경
 * 3. 테스트 실행: ./gradlew test --tests "*SendEmailUseCaseIntegrationTest"
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
@DisplayName("SendEmailUseCase 통합 테스트 (실제 메일 전송)")
class SendEmailUseCaseIntegrationTest {

    @Autowired
    private SendEmailUseCase sendEmailUseCase;

    /**
     * 테스트용 이메일 주소
     * application-local.yml에서 주입받습니다.
     * 
     * 우선순위:
     * 1. 환경변수 TEST_RECIPIENT_EMAIL, TEST_SENDER_EMAIL (가장 우선)
     * 2. application-local.yml의 test.mail 설정
     */
    @Value("${test.mail.recipient-email:wanted.aidk@gmail.com}")
    private String testRecipientEmail;

    @Value("${test.mail.sender-email:whffu78@gmail.com}")
    private String testSenderEmail;

    /**
     * 실제 사용할 이메일 주소 (환경변수 우선, 없으면 @Value로 주입받은 값 사용)
     */
    private String getRecipientEmail() {
        String envEmail = System.getenv("TEST_RECIPIENT_EMAIL");
        if (envEmail != null && !envEmail.isEmpty()) {
            return envEmail;
        }
        return testRecipientEmail != null && !testRecipientEmail.isEmpty() 
                ? testRecipientEmail 
                : "wanted.aidk@gmail.com";
    }

    private String getSenderEmail() {
        String envEmail = System.getenv("TEST_SENDER_EMAIL");
        if (envEmail != null && !envEmail.isEmpty()) {
            return envEmail;
        }
        return testSenderEmail != null && !testSenderEmail.isEmpty() 
                ? testSenderEmail 
                : "whffu78@gmail.com";
    }

    @Test
    @DisplayName("템플릿을 사용하여 실제 메일을 발송할 수 있다 (환영 메일)")
    void execute_success_with_template_welcome_mail() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.external(getRecipientEmail());
        Sender sender = Sender.system(getSenderEmail());
        WelcomeMailVariable variables = new WelcomeMailVariable(
                "테스트 사용자",
                "https://example.com/login"
        );

        // when & then
        assertThatCode(() -> sendEmailUseCase.execute(templateCode, recipient, sender, variables))
                .doesNotThrowAnyException();

        System.out.println("✅ 환영 메일이 발송되었습니다. 수신자: " + getRecipientEmail());
    }

    @Test
    @DisplayName("템플릿을 사용하여 실제 메일을 발송할 수 있다 (이메일 인증)")
    void execute_success_with_template_email_verification() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.EMAIL_VERIFICATION;
        Recipient recipient = Recipient.external(getRecipientEmail());
        Sender sender = Sender.system(getSenderEmail());
        EmailVerificationVariable variables = new EmailVerificationVariable(
                "테스트 사용자",
                "https://example.com/verify?token=test-token-123",
                30
        );

        // when & then
        assertThatCode(() -> sendEmailUseCase.execute(templateCode, recipient, sender, variables))
                .doesNotThrowAnyException();

        System.out.println("✅ 이메일 인증 메일이 발송되었습니다. 수신자: " + getRecipientEmail());
    }

    @Test
    @DisplayName("템플릿 없이 직접 메일을 발송할 수 있다")
    void execute_success_without_template() {
        // given
        Recipient recipient = Recipient.external(getRecipientEmail());
        Sender sender = Sender.system(getSenderEmail());
        String subject = "[테스트] 직접 발송 메일";
        String content = "<html><body><h1>테스트 메일</h1><p>이것은 통합 테스트에서 발송한 메일입니다.</p></body></html>";

        // when & then
        assertThatCode(() -> sendEmailUseCase.execute(recipient, sender, subject, content))
                .doesNotThrowAnyException();

        System.out.println("✅ 직접 발송 메일이 발송되었습니다. 수신자: " + getRecipientEmail());
    }

    @Test
    @DisplayName("내부인(회원) 수신자로 메일을 발송할 수 있다")
    void execute_success_with_internal_recipient() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.internal(getRecipientEmail(), 1L); // 내부인(회원)
        Sender sender = Sender.system(getSenderEmail());
        WelcomeMailVariable variables = new WelcomeMailVariable(
                "테스트 회원",
                "https://example.com/login"
        );

        // when & then
        assertThatCode(() -> sendEmailUseCase.execute(templateCode, recipient, sender, variables))
                .doesNotThrowAnyException();

        System.out.println("✅ 내부인 수신자로 메일이 발송되었습니다. 수신자: " + getRecipientEmail());
    }
}

