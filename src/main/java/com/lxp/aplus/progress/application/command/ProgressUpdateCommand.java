package com.lxp.aplus.progress.application.command;

import lombok.Builder;

@Builder
public record ProgressUpdateCommand(
    Long enrollmentId,
    Long resourceId,
    int watchedDuration
) {

}
