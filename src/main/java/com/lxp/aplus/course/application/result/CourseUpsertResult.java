package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Course;
import lombok.Builder;

@Builder
public record CourseUpsertResult(
        Long courseId
) {
    public static CourseUpsertResult from(Course course) {
        return CourseUpsertResult.builder()
                .courseId(course.getId())
                .build();
    }
}
