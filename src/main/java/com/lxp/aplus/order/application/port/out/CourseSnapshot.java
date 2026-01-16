package com.lxp.aplus.order.application.port.out;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record CourseSnapshot(
        Long courseId,
        String courseTitle,
        CourseStatus courseStatus,
        String instructorName,
        String thumbnailUrl,
        int price
) {

    public static CourseSnapshot of(Course course, User instructor) {

        return CourseSnapshot.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseStatus(course.getCourseStatus())
                .instructorName(instructor.getNickName())
                .thumbnailUrl(course.getThumbnailResourceKey())
                .price(course.getPrice())
                .build();
    }
}
