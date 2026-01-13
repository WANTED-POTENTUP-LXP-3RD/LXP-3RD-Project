package com.lxp.aplus.mail.domain.template.variable;

import java.util.Map;

/**
 * 주문 확인 메일 템플릿 변수
 */
public record OrderConfirmationVariable(
        String userName,
        String orderId,
        String orderDate,
        Integer totalAmount,
        String orderDetailUrl
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "orderId", orderId,
                "orderDate", orderDate,
                "totalAmount", totalAmount,
                "orderDetailUrl", orderDetailUrl
        );
    }
}

