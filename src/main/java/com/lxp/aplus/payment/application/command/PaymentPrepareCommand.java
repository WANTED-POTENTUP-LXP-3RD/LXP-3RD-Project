package com.lxp.aplus.payment.application.command;

import lombok.Builder;

import java.util.List;

@Builder
public record PaymentPrepareCommand(
        Long userId,
        List<Long> courseIds
) {
}
