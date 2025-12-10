package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.SectionCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SectionCreateRequest(
        @NotBlank(message = "섹션 제목은 필수입니다.")
        String title,
        @NotNull(message = "섹션 순서는 필수입니다.")
        Integer orderIndex
) {
    public SectionCreateCommand toCommand() {
        return SectionCreateCommand.builder()
                .title(this.title)
                .orderIndex(this.orderIndex)
                .build();
    }
}
