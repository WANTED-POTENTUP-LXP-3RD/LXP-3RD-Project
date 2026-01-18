package com.lxp.aplus.course.application.port.in.dto;

import com.lxp.aplus.course.domain.LectureResourceV2;

public record ResourceDuration(
        int totalDurationSeconds
) {
    public static ResourceDuration from(LectureResourceV2 resource) {
        int duration = (resource.getVideoDuration() != null)
                ? resource.getVideoDuration().getDuration() : 0;
        return new ResourceDuration(duration);
    }
}
