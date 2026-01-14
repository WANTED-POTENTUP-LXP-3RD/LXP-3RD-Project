package com.lxp.aplus.payment.presentation.request;

import com.lxp.aplus.payment.application.command.PaymentPrepareCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PaymentPrepareRequest(
        @NotNull(message = "items는 필수입니다.")
        @Size(min = 1, message = "최소 1개 이상의 상품을 선택해야합니다.")
        @Valid
        List<Item> items
) {
    public record Item(
            @NotNull(message = "courseId는 필수입니다.")
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
