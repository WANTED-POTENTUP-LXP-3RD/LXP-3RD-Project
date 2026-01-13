package com.lxp.aplus.order.application.port.out;

import com.lxp.aplus.course.domain.CourseStatus;

public record CourseSalesStatus(
        Integer price,
        CourseStatus status
) {
}
