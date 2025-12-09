package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Course;
import lombok.Builder;

@Builder
public record CourseResult(
        Long courseId
) {
    public static CourseResult from(Course course) {
        return CourseResult.builder()
                .courseId(course.getId())
                .build();
    }
}
