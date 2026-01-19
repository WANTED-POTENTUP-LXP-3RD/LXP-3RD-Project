package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.LectureResourceV2;
import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record LectureResourceResponse(
        Long resourceId,
        ResourceType resourceType,
        boolean isDownloadable,
        String fileUrl
) {
    public static LectureResourceResponse from(LectureResourceV2 resource) {
        return LectureResourceResponse.builder()
                .resourceId(resource.getId())
                .resourceType(resource.getResourceType())
                .fileUrl(resource.getFileKey())
                .isDownloadable(resource.isDownloadable())
                .build();
    }
}
