package com.lxp.aplus.progress.application.command;

public record ProgressUpdateCommand(
    Long enrollmentId,
    Long resourceId,
    int watchedDuration
) {
    public static ProgressUpdateCommand of(Long enrollmentId, Long resourceId, int watchedDuration) {
        return new ProgressUpdateCommand(enrollmentId, resourceId, watchedDuration);
    }
}
