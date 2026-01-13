package com.lxp.aplus.mail.domain.template.variable;

import java.util.Map;

/**
 * 회원가입 환영 메일 템플릿 변수
 */
public record WelcomeMailVariable(
        String userName,
        String loginUrl
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "loginUrl", loginUrl
        );
    }
}

