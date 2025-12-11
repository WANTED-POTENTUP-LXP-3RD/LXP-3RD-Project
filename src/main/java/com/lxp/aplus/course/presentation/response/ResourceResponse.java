package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.course.domain.ResourceType;
import lombok.Builder;

@Builder
public record ResourceResponse(
        ResourceType resourceType,
        String fileUrl,
        boolean isDownloadable
) {
    public static ResourceResponse from(LectureResource resource) {
        return ResourceResponse.builder()
                .resourceType(resource.getResourceType())
                .fileUrl(resource.getFileKey())
                .isDownloadable(resource.isDownloadable())
                .build();
    }
}
