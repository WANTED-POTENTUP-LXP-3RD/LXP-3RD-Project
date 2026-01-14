package com.lxp.aplus.progress.application.port;

/**
* 진도 업데이트에 필요한 데이터를 제공하는 것
* */
public record LectureDurationDto(
    int totalDurationSeconds
) {
}
