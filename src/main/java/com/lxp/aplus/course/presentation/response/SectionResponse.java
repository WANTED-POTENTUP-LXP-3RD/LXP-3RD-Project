package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.SectionResult;
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
    public static SectionResponse from(SectionResult result) {
        return SectionResponse.builder()
                .sectionId(result.sectionId())
                .title(result.title())
                .order(result.order())
                .lectures(result.lectures().stream()
                        .map(LectureResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
