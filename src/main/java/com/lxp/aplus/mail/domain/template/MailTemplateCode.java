package com.lxp.aplus.mail.domain.template;

import com.lxp.aplus.mail.domain.template.variable.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 메일 템플릿 코드 Enum
 * 
 * 각 템플릿 코드는 해당하는 변수 모델 클래스를 가집니다.
 * 이를 통해 컴파일 타임에 타입 안전성을 보장합니다.
 */
@Getter
@RequiredArgsConstructor
public enum MailTemplateCode {
    WELCOME_MAIL("회원가입 환영 메일", WelcomeMailVariable.class),
    PASSWORD_RESET("비밀번호 재설정", PasswordResetVariable.class),
    EMAIL_VERIFICATION("이메일 인증", EmailVerificationVariable.class),
    ORDER_CONFIRMATION("주문 확인", OrderConfirmationVariable.class),
    PAYMENT_COMPLETED("결제 완료", PaymentCompletedVariable.class);

    private final String description;
    private final Class<? extends com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable> variableClass;

    /**
     * Enum 이름을 문자열로 반환 (DB 저장용)
     */
    public String getCode() {
        return this.name();
    }
    
    /**
     * 이 템플릿 코드에 해당하는 변수 모델 클래스 반환
     */
    public Class<? extends com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable> getVariableClass() {
        return variableClass;
    }
}

