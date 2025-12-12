package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

@Builder
public record CoursePublishResponse(
        Long id,
        String title,
        CourseStatus courseState
) {
    public static CoursePublishResponse from(Course course) {
        return CoursePublishResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .courseState(course.getCourseStatus())
                .build();
    }
}
