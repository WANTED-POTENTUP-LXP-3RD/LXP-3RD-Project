package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.course.domain.LectureResourceV2;
import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record LectureResourceResponse(
        ResourceType resourceType,
        boolean isDownloadable,
        String fileKey
) {
    public static LectureResourceResponse from(LectureResourceV2 resource) {
        return LectureResourceResponse.builder()
                .resourceType(resource.getResourceType())
                .fileKey(resource.getFileKey())
                .isDownloadable(resource.isDownloadable())
                .build();
    }
}
