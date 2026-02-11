package com.lxp.aplus.order.application.port.out.dto;

import com.lxp.aplus.course.domain.CourseStatus;
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
}
