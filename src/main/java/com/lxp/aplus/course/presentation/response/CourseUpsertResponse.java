package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Course;
import lombok.Builder;

@Builder
public record CourseUpsertResponse(
        Long courseId
) {
    public static CourseUpsertResponse from(Course course) {
        return CourseUpsertResponse.builder()
                .courseId(course.getId())
                .build();
    }
}
