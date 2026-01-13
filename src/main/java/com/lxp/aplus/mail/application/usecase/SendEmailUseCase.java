package com.lxp.aplus.mail.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.MailErrorCode;
import com.lxp.aplus.mail.domain.Mail;
import com.lxp.aplus.mail.domain.MailSender;
import com.lxp.aplus.mail.domain.Recipient;
import com.lxp.aplus.mail.domain.Sender;
import com.lxp.aplus.mail.domain.template.MailTemplate;
import com.lxp.aplus.mail.domain.template.MailTemplateCode;
import com.lxp.aplus.mail.domain.template.MailTemplateRepository;
import com.lxp.aplus.mail.application.MailTemplateRenderer;
import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메일 발송 UseCase
 * 
 * 템플릿을 사용한 메일 발송을 오케스트레이션.
 * - 템플릿 조회
 * - 템플릿 변수 렌더링 (변수가 있는 경우)
 * - 메일 발송
 * 
 * 변수가 없는 템플릿 지원 (variables = null).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailUseCase {
    
    private final MailSender mailSender;
    private final MailTemplateRepository mailTemplateRepository;
    private final MailTemplateRenderer templateRenderer;
    
    /**
     * 템플릿을 사용하여 메일 발송
     * 
     * 템플릿 코드에서 자동으로 변수 타입을 추론하여 검증.
     * 
     * @param templateCode 템플릿 코드 (변수 타입을 결정)
     * @param recipient 수신자
     * @param sender 발신자
     * @param variables 템플릿 변수 (templateCode에서 타입을 자동 추론하여 검증, null 가능)
     */
    @Transactional(readOnly = true)
    public void execute(
            MailTemplateCode templateCode,
            Recipient recipient,
            Sender sender,
            MailTemplateVariable variables
    ) {
        // templateCode에서 변수 타입을 자동 추론하여 검증
        MailTemplateVariable validatedVariables = validateVariables(templateCode, variables);
        
        // 템플릿 조회
        MailTemplate template = mailTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new BusinessException(MailErrorCode.MAIL_TEMPLATE_NOT_FOUND));
        
        // 템플릿 렌더링 (제목은 변수 없이 그대로, 내용만 변수 렌더링)
        String subject = template.getSubject();
        String renderedContent = template.renderContent(templateRenderer, validatedVariables);
        
        // 메일 생성
        Mail mail = Mail.create(
                recipient,
                sender,
                subject,
                renderedContent
        );
        
        // 메일 발송
        send(mail);
    }
    
    /**
     * 템플릿 없이 직접 메일 발송
     * 
     * @param recipient 수신자
     * @param sender 발신자
     * @param subject 메일 제목
     * @param content 메일 내용 (HTML)
     */
    public void execute(
            Recipient recipient,
            Sender sender,
            String subject,
            String content
    ) {
        Mail mail = Mail.create(
                recipient,
                sender,
                subject,
                content
        );
        
        send(mail);
    }
    
    /**
     * 템플릿 변수 검증
     * 
     * @param templateCode 템플릿 코드
     * @param variables 템플릿 변수
     * @return 검증된 변수
     * @throws BusinessException 변수가 null이거나 타입이 맞지 않는 경우
     */
    private MailTemplateVariable validateVariables(
            MailTemplateCode templateCode,
            MailTemplateVariable variables
    ) {
        // 변수가 null인 경우
        if (variables == null)
            throw new BusinessException(MailErrorCode.MISSING_TEMPLATE_VARIABLE);
        
        // 변수 타입 검증
        Class<? extends MailTemplateVariable> expectedClass = templateCode.getVariableClass();
        if (!expectedClass.isInstance(variables)) {
            log.warn("템플릿 변수 타입 불일치: 템플릿 코드={}, 예상 타입={}, 실제 타입={}",
                    templateCode, expectedClass.getSimpleName(), variables.getClass().getSimpleName());
            throw new BusinessException(MailErrorCode.INVALID_TEMPLATE_VARIABLE_TYPE);
        }
        
        return templateCode.getVariableClass().cast(variables);
    }
    
    /**
     * 메일 발송 실행
     */
    private void send(Mail mail) {
        try {
            mailSender.send(mail);
            log.info("메일 발송 완료: 수신자={}, 제목={}", mail.getRecipientEmail(), mail.getSubject());
        } catch (Exception e) {
            log.error("메일 발송 실패: 수신자={}, 제목={}", mail.getRecipientEmail(), mail.getSubject(), e);
            throw e;
        }
    }
}

