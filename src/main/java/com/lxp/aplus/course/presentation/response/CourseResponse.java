package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.CourseResult;
import lombok.Builder;

@Builder
public record CourseResponse(
        Long courseId
) {
    public static CourseResponse from(CourseResult result) {
        return CourseResponse.builder()
                .courseId(result.courseId())
                .build();
    }
}
