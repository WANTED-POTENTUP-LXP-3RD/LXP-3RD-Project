package com.lxp.aplus.payment.application.command;

import java.util.List;

public record PaymentPrepareCommand(
        Long userId,
        List<Long> courseIds
) {
}
