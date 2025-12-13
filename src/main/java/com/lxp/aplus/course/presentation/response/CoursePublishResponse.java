package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.CoursePublishResult;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

@Builder
public record CoursePublishResponse(
        Long id,
        String title,
        CourseStatus courseState
) {
    public static CoursePublishResponse from(CoursePublishResult result) {
        return CoursePublishResponse.builder()
                .id(result.id())
                .title(result.title())
                .courseState(result.courseState())
                .build();
    }
}
