package com.lxp.aplus.course.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LectureResourceRequest(
        boolean isDownloadable
) {
}
