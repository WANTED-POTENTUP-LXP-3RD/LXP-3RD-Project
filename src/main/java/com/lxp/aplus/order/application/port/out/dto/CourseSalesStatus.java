package com.lxp.aplus.order.application.port.out.dto;

import com.lxp.aplus.course.domain.CourseStatus;

public record CourseSalesStatus(
        Integer price,
        CourseStatus status
) {
}
