package com.lxp.aplus.progress.application.command;

import lombok.Builder;

@Builder
public record ProgressUpdateRequest(
    Long resourceId,
    int watchedDuration
) {}
