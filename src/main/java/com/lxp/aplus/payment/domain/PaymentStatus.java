package com.lxp.aplus.payment.domain;

public enum PaymentStatus {
    // 결제 대기
    PENDING,
    // 결제 승인 성공
    APPROVED,
    // 결제 승인 실패
    FAILED,
    // 결제 승인 취소
    CANCELED,
    // 결제 환불
    REFUNDED
}
