package com.lxp.aplus.course.application.port.in;

import com.lxp.aplus.course.application.port.in.dto.ResourceDuration;
import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;

import java.util.List;

public interface CourseQueryToProgressUseCase {
    ResourceDuration getLectureDuration(Long resourceId);
    List<ResourceSummary> findLectureSummariesByCourseId(Long courseId);
}
