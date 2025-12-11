package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Section;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SectionResponse(
        Long sectionId,
        String title,
        int order,
        List<LectureResponse> lectures
) {
    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .order(section.getOrderIndex())
                .lectures(section.getLectures().stream()
                        .map(LectureResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
