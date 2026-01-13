package com.lxp.aplus.mail.domain.template.variable;

import java.util.Map;

/**
 * 비밀번호 재설정 메일 템플릿 변수
 */
public record PasswordResetVariable(
        String userName,
        String resetLink,
        Integer expirationMinutes
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "resetLink", resetLink,
                "expirationMinutes", expirationMinutes
        );
    }
}

