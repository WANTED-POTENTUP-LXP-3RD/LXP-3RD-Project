package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record LectureResourceResult(
        Long resourceId,
        ResourceType resourceType,
        boolean isDownloadable,
        String fileUrl
) {
}
