package com.lxp.aplus.payment.application.command;

import java.util.List;

public record PreparePaymentCommand(
        Long userId,
        List<Long> courseIds
) {
}
