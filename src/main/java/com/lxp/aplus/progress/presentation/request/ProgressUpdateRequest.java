package com.lxp.aplus.progress.presentation.request;

import lombok.Builder;

@Builder
public record ProgressUpdateRequest(
    Long resourceId,
    int watchedDuration
) {}
