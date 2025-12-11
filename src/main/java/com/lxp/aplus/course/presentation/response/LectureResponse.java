package com.lxp.aplus.course.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.course.domain.Lecture;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LectureResponse(
        Long lectureId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        List<ResourceResponse> resources
) {
    public static LectureResponse from(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .resources(lecture.getLectureResources().stream()
                        .map(ResourceResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
