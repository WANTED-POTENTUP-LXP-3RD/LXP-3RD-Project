package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CreateLectureResourceCommand;
import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import com.lxp.aplus.course.application.vo.UploadFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LectureUpdateRequest(
        @NotNull(message = "제목은 필수입니다.")
        String title,

        Integer totalDurationSeconds,

        boolean isPreview,

        @Min(value = 1, message = "순서 인덱스는 1 이상이어야 합니다.")
        int orderIndex,

        @NotNull(message = "강의 자료는 필수입니다.")
        LectureResourceRequest resource
) {
    public UpdateLectureCommand toCommand(UploadFile uploadFile) {
        return UpdateLectureCommand.builder()
                .title(this.title)
                .totalDurationSeconds(this.totalDurationSeconds)
                .isPreview(this.isPreview)
                .orderIndex(this.orderIndex)
                .resource(
                        CreateLectureResourceCommand.builder()
                                .isDownloadable(this.resource.isDownloadable())
                                .uploadFile(uploadFile)
                                .build()
                )
                .build();
    }
}
