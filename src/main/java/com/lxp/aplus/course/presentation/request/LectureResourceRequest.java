package com.lxp.aplus.course.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LectureResourceRequest(
        @NotNull(message = "다운로드 가능 여부는 필수입니다.")
        boolean isDownloadable
) {
}
