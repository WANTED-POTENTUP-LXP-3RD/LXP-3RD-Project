package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Section;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SectionResult(
        Long sectionId,
        String title,
        int order,
        List<LectureResult> lectures
) {
    public static SectionResult from(Section section) {
        return SectionResult.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .order(section.getOrderIndex())
                .lectures(section.getLectures().stream()
                        .map(LectureResult::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
