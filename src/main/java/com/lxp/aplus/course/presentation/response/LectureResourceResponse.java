package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record LectureResourceResponse(
        ResourceType resourceType,
        boolean isDownloadable,
        String fileUrl
) {
    public static LectureResourceResponse from(LectureResource resource) {
        return LectureResourceResponse.builder()
                .resourceType(resource.getResourceType())
                .fileUrl(resource.getFileKey())
                .isDownloadable(resource.isDownloadable())
                .build();
    }
}
