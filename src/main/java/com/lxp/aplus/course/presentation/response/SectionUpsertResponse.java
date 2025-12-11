package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Section;
import lombok.Builder;

@Builder
public record SectionUpsertResponse(
        Long sectionId,
        String title,
        int orderIndex
) {
    public static SectionUpsertResponse from(Section section) {
        return SectionUpsertResponse.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .build();
    }
}
