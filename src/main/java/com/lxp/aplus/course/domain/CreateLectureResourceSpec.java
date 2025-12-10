package com.lxp.aplus.course.domain;

import lombok.Builder;

@Builder
public record CreateLectureResourceSpec(
        boolean isDownloadable
) {
}
