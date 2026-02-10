package com.lxp.aplus.course.application.internal.dto;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

@Builder
public record CourseInternalResult(
        Long id,
        String title,
        int price,
        CourseStatus courseStatus,
        Long instructorId,
        Long categoryId
) {
    public static CourseInternalResult from(Course course) {
        return CourseInternalResult.builder()
                .id(course.getId())
                .title(course.getTitle())
                .price(course.getPrice())
                .courseStatus(course.getCourseStatus())
                .instructorId(course.getInstructorId())
                .categoryId(course.getCategoryId())
                .build();
    }
}
