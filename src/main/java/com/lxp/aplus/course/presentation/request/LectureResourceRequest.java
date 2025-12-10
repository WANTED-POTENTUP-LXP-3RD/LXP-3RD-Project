package com.lxp.aplus.course.presentation.request;

import lombok.Builder;

@Builder
public record LectureResourceRequest(
        boolean isDownloadable
) {
}
