package com.lxp.aplus.payment.application.result;

import com.lxp.aplus.payment.domain.Payment;
import com.lxp.aplus.payment.domain.PaymentStatus;

public record PaymentResult(
        String paymentId,
        PaymentStatus paymentStatus
) {
    public static PaymentResult from(Payment payment) {
        return new PaymentResult(
                payment.getPaymentId(),
                payment.getPaymentStatus()
        );
    }
}
