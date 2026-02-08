package com.lxp.aplus.progress.application.internal.dto;

import lombok.Builder;

@Builder
public record ProgressSummaryInternalResult(
        int overallProgressRate
) {
}
