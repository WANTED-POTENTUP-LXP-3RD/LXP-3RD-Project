package com.lxp.aplus.mail.domain;

/**
 * 메일 발송 포트 인터페이스
 * 다양한 메일 발송 구현체를 추상화
 * 
 * 구현체 예시:
 * - GmailSmtpMailSender (Gmail SMTP)
 * - AwsSesMailSender (AWS SES)
 * - SendGridMailSender (SendGrid)
 */
public interface MailSender {
    
    /**
     * 메일 발송
     * 
     * @param mail 발송할 메일 도메인 객체
     * @throws MailSendException 메일 발송 실패 시
     */
    void send(Mail mail);
}

