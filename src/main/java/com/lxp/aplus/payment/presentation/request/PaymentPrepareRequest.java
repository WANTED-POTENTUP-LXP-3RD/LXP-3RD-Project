package com.lxp.aplus.payment.presentation.request;

import com.lxp.aplus.payment.application.command.PaymentPrepareCommand;

import java.util.List;

public record PaymentPrepareRequest(
        List<Item> items
) {
    public record Item(
            Long courseId
    ) {}

    // [팩토리 메서드]
    // DTO가 스스로 Command 객체로 변환하는 책임을 지게 되어 Controller가 간결해진다.
    public PaymentPrepareCommand toCommand(Long userId) {
        List<Long> courseIds = items().stream()
                .map(Item::courseId)
                .toList();

        return PaymentPrepareCommand.builder()
                .userId(userId)
                .courseIds(courseIds)
                .build();
    }
}
