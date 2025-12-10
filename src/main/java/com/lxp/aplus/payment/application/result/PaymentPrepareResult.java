package com.lxp.aplus.payment.application.result;

import com.lxp.aplus.payment.domain.Payment;
import com.lxp.aplus.payment.domain.PaymentStatus;

public record PaymentPrepareResult(
        String paymentId,
        PaymentStatus paymentStatus
) {
    public static PaymentPrepareResult from(Payment payment) {
        return new PaymentPrepareResult(
                payment.getPaymentId(),
                payment.getPaymentStatus()
        );
    }
}
