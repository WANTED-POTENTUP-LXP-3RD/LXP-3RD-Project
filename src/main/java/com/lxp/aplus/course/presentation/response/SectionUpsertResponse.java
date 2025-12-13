package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.SectionUpsertResult;
import lombok.Builder;

@Builder
public record SectionUpsertResponse(
        Long sectionId,
        String title,
        int orderIndex
) {
    public static SectionUpsertResponse from(SectionUpsertResult result) {
        return SectionUpsertResponse.builder()
                .sectionId(result.sectionId())
                .title(result.title())
                .orderIndex(result.orderIndex())
                .build();
    }
}
