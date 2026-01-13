package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CreateLectureCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LectureCreateRequest(
        @NotNull(message = "제목은 필수입니다.")
        String title,

        @NotNull(message = "미리보기 여부는 필수 값입니다.")
        Boolean isPreview,

        @Min(value = 1, message = "순서 인덱스는 1 이상이어야 합니다.")
        int orderIndex,

        @NotNull(message = "리소스 키는 필수 입니다.")
        String resourceKey
) {
    public CreateLectureCommand toCommand() {
        return CreateLectureCommand.builder()
                .title(this.title)
                .isPreview(this.isPreview)
                .orderIndex(this.orderIndex)
                .resourceKey(this.resourceKey)
                .build();
    }
}


