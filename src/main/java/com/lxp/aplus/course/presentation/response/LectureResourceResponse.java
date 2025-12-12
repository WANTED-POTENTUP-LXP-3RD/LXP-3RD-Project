package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record LectureResourceResponse(
        ResourceType resourceType,
        boolean isDownloadable,
        String fileUrl
) {
}
