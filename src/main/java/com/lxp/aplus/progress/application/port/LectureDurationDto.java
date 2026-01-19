package com.lxp.aplus.progress.application.port;

import com.lxp.aplus.course.application.port.in.dto.ResourceDuration;

/**
* 진도 업데이트에 필요한 데이터를 제공하는 것
* */
public record LectureDurationDto(
    int totalDurationSeconds
) {
    public static LectureDurationDto from(ResourceDuration resource) {
        return new LectureDurationDto(
                resource.totalDurationSeconds()
        );
    }
}
