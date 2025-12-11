package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LectureUpdateRequest(
        @NotNull(message = "제목은 필수입니다.")
        String title,

        @NotNull(message = "총 재생 시간은 필수입니다.")
        Integer totalDurationSeconds,

        @NotNull(message = "미리보기 여부는 필수입니다.")
        Boolean isPreview,

        @Min(value = 1, message = "순서 인덱스는 1 이상이어야 합니다.")
        int orderIndex,

        @NotNull(message = "강의 자료는 필수입니다.")
        LectureResourceRequest resource
) {
    public UpdateLectureCommand toCommand() {
        return UpdateLectureCommand.builder()
                .title(this.title)
                .totalDurationSeconds(this.totalDurationSeconds)
                .isPreview(this.isPreview)
                .resource(this.resource)
                .build();
    }
}
