package com.lxp.aplus.mail.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.MailErrorCode;
import com.lxp.aplus.mail.application.MailTemplateRenderer;
import com.lxp.aplus.mail.domain.Mail;
import com.lxp.aplus.mail.domain.MailSender;
import com.lxp.aplus.mail.domain.Recipient;
import com.lxp.aplus.mail.domain.Sender;
import com.lxp.aplus.mail.domain.template.MailTemplate;
import com.lxp.aplus.mail.domain.template.MailTemplateCode;
import com.lxp.aplus.mail.domain.template.MailTemplateRepository;
import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;
import com.lxp.aplus.mail.domain.template.variable.WelcomeMailVariable;
import com.lxp.aplus.mail.domain.template.variable.PasswordResetVariable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SendEmailUseCase 테스트")
class SendEmailUseCaseTest {

    @Mock
    private MailSender mailSender;

    @Mock
    private MailTemplateRepository mailTemplateRepository;

    @Mock
    private MailTemplateRenderer templateRenderer;

    @InjectMocks
    private SendEmailUseCase sendEmailUseCase;

    @Test
    @DisplayName("템플릿을 사용하여 메일을 발송할 수 있다")
    void execute_success_with_template() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        WelcomeMailVariable variables = new WelcomeMailVariable("홍길동", "https://example.com/login");

        MailTemplate template = MailTemplate.create(
                templateCode,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>안녕하세요 {{userName}}님</html>"
        );

        String renderedContent = "<html>안녕하세요 홍길동님</html>";

        when(mailTemplateRepository.findByTemplateCode(templateCode))
                .thenReturn(Optional.of(template));
        when(templateRenderer.render(template.getHtmlContent(), variables))
                .thenReturn(renderedContent);

        // when
        sendEmailUseCase.execute(templateCode, recipient, sender, variables);

        // then
        ArgumentCaptor<Mail> mailCaptor = ArgumentCaptor.forClass(Mail.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getRecipient()).isEqualTo(recipient);
        assertThat(sentMail.getSender()).isEqualTo(sender);
        assertThat(sentMail.getSubject()).isEqualTo("환영합니다!");
        assertThat(sentMail.getContent()).isEqualTo(renderedContent);

        verify(mailTemplateRepository, times(1)).findByTemplateCode(templateCode);
        verify(templateRenderer, times(1)).render(template.getHtmlContent(), variables);
    }

    @Test
    @DisplayName("템플릿이 없으면 BusinessException을 던진다")
    void execute_fail_if_template_not_found() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        WelcomeMailVariable variables = new WelcomeMailVariable("홍길동", "https://example.com/login");

        when(mailTemplateRepository.findByTemplateCode(templateCode))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sendEmailUseCase.execute(templateCode, recipient, sender, variables))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MailErrorCode.MAIL_TEMPLATE_NOT_FOUND);

        verify(mailSender, never()).send(any(Mail.class));
    }

    @Test
    @DisplayName("변수가 null이면 BusinessException을 던진다")
    void execute_fail_if_variables_is_null() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");

        // when & then
        assertThatThrownBy(() -> sendEmailUseCase.execute(templateCode, recipient, sender, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MailErrorCode.MISSING_TEMPLATE_VARIABLE);

        verify(mailTemplateRepository, never()).findByTemplateCode(any());
        verify(mailSender, never()).send(any(Mail.class));
    }

    @Test
    @DisplayName("변수 타입이 템플릿 코드와 맞지 않으면 BusinessException을 던진다")
    void execute_fail_if_variable_type_mismatch() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL; // WelcomeMailVariable 기대
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        PasswordResetVariable wrongVariables = new PasswordResetVariable("홍길동", "https://example.com/reset", 30);

        // when & then
        assertThatThrownBy(() -> sendEmailUseCase.execute(templateCode, recipient, sender, wrongVariables))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MailErrorCode.INVALID_TEMPLATE_VARIABLE_TYPE);

        verify(mailTemplateRepository, never()).findByTemplateCode(any());
        verify(mailSender, never()).send(any(Mail.class));
    }

    @Test
    @DisplayName("템플릿 없이 직접 메일을 발송할 수 있다")
    void execute_success_without_template() {
        // given
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        String subject = "직접 발송 메일";
        String content = "<html>직접 작성한 내용</html>";

        // when
        sendEmailUseCase.execute(recipient, sender, subject, content);

        // then
        ArgumentCaptor<Mail> mailCaptor = ArgumentCaptor.forClass(Mail.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getRecipient()).isEqualTo(recipient);
        assertThat(sentMail.getSender()).isEqualTo(sender);
        assertThat(sentMail.getSubject()).isEqualTo(subject);
        assertThat(sentMail.getContent()).isEqualTo(content);

        verify(mailTemplateRepository, never()).findByTemplateCode(any());
        verify(templateRenderer, never()).render(anyString(), any(MailTemplateVariable.class));
    }

    @Test
    @DisplayName("메일 발송 실패 시 예외를 다시 던진다")
    void execute_fail_if_mail_send_fails() {
        // given
        MailTemplateCode templateCode = MailTemplateCode.WELCOME_MAIL;
        Recipient recipient = Recipient.internal("user@example.com", 1L);
        Sender sender = Sender.system("noreply@example.com");
        WelcomeMailVariable variables = new WelcomeMailVariable("홍길동", "https://example.com/login");

        MailTemplate template = MailTemplate.create(
                templateCode,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>안녕하세요 {{userName}}님</html>"
        );

        String renderedContent = "<html>안녕하세요 홍길동님</html>";

        when(mailTemplateRepository.findByTemplateCode(templateCode))
                .thenReturn(Optional.of(template));
        when(templateRenderer.render(template.getHtmlContent(), variables))
                .thenReturn(renderedContent);
        doThrow(new RuntimeException("SMTP 연결 실패"))
                .when(mailSender).send(any(Mail.class));

        // when & then
        assertThatThrownBy(() -> sendEmailUseCase.execute(templateCode, recipient, sender, variables))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SMTP 연결 실패");

        verify(mailSender, times(1)).send(any(Mail.class));
    }
}

