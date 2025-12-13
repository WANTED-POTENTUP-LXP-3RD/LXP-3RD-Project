package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

@Builder
public record CoursePublishResult(
        Long id,
        String title,
        CourseStatus courseState
) {
    public static CoursePublishResult from(Course course) {
        return CoursePublishResult.builder()
                .id(course.getId())
                .title(course.getTitle())
                .courseState(course.getCourseStatus())
                .build();
    }
}
