package com.lxp.aplus.mail.domain.template.variable;

import java.util.Map;

/**
 * 결제 완료 메일 템플릿 변수
 */
public record PaymentCompletedVariable(
        String userName,
        String orderId,
        String paymentDate,
        Integer paymentAmount,
        String paymentMethod,
        String courseNames
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "orderId", orderId,
                "paymentDate", paymentDate,
                "paymentAmount", paymentAmount,
                "paymentMethod", paymentMethod,
                "courseNames", courseNames
        );
    }
}

