package com.lxp.aplus.mail.infrastructure.smtp;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.MailErrorCode;
import com.lxp.aplus.mail.domain.Mail;
import com.lxp.aplus.mail.domain.MailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Gmail SMTP를 사용한 메일 발송 어댑터
 * 
 * Gmail SMTP 서버를 통해 메일을 발송.
 * application.yml에서 Gmail SMTP 설정 구성.
 * 
 * 추후 다른 SMTP 서비스 추가 개발시 MailSender 인터페이스를 구현하는
 * 새로운 어댑터 구현
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GmailSmtpMailSender implements MailSender {
    
    private final JavaMailSender javaMailSender;
    
    @Override
    public void send(Mail mail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 발신자 설정
            helper.setFrom(mail.getSenderEmail());
            
            // 수신자 설정
            helper.setTo(mail.getRecipientEmail());
            
            // 제목 설정
            helper.setSubject(mail.getSubject());
            
            // 내용 설정 (HTML)
            helper.setText(mail.getContent(), true);
            
            // 메일 발송
            javaMailSender.send(message);
            
            log.info("메일 발송 성공: 수신자={}, 제목={}", mail.getRecipientEmail(), mail.getSubject());
            
        } catch (MessagingException e) {
            log.error("메일 발송 실패: 수신자={}, 제목={}", mail.getRecipientEmail(), mail.getSubject(), e);
            throw new BusinessException(MailErrorCode.MAIL_SEND_FAILED);
        } catch (Exception e) {
            log.error("메일 발송 중 예상치 못한 오류 발생: 수신자={}, 제목={}", 
                    mail.getRecipientEmail(), mail.getSubject(), e);
            throw new BusinessException(MailErrorCode.MAIL_SEND_FAILED);
        }
    }
}

