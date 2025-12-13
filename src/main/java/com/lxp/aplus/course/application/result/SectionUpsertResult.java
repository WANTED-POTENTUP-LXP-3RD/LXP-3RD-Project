package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Section;
import lombok.Builder;

@Builder
public record SectionUpsertResult(
        Long sectionId,
        String title,
        int orderIndex
) {
    public static SectionUpsertResult from(Section section) {
        return SectionUpsertResult.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .build();
    }
}
