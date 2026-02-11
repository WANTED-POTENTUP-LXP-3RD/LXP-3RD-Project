package com.lxp.aplus.progress.application.port.in;

import com.lxp.aplus.progress.application.dto.response.CourseProgressResponse;

public interface ProgressQueryUseCase {
    CourseProgressResponse getCourseProgress(Long userId, Long courseId);
}
