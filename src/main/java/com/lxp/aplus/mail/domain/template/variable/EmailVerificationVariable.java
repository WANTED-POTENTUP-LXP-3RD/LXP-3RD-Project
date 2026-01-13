package com.lxp.aplus.mail.domain.template.variable;

import java.util.Map;

/**
 * 이메일 인증 메일 템플릿 변수
 */
public record EmailVerificationVariable(
        String userName,
        String verificationLink,
        Integer expirationMinutes
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "verificationLink", verificationLink,
                "expirationMinutes", expirationMinutes
        );
    }
}

