package com.lxp.aplus.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("결제 대기"),
    APPROVED("결제 승인"),
    FAILED("결제 승인"),
    CANCELED("결제 취소"),
    REFUNDED("결제 환불");

    private final String description;
}
