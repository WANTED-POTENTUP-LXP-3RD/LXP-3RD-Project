package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.CourseUpsertResult;
import lombok.Builder;

@Builder
public record CourseUpsertResponse(
        Long courseId
) {
    public static CourseUpsertResponse from(CourseUpsertResult result) {
        return CourseUpsertResponse.builder()
                .courseId(result.courseId())
                .build();
    }
}
