package com.lxp.aplus.progress.application.port;

public record LectureDetailInfo(
    Long resourceId,
    String title,
    int totalDurationSeconds
) {
}
