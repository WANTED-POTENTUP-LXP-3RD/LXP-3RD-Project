package com.lxp.aplus.course.domain;

import lombok.Builder;

@Builder
public record UpdateLectureResourceSpec(
        boolean isDownloadable
) {
}
