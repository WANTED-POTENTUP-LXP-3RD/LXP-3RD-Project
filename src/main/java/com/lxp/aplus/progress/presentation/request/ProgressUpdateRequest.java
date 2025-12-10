package com.lxp.aplus.progress.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProgressUpdateRequest(
        @NotNull(message = "리소스 ID는 필수입니다.")
        Long resourceId,
        @NotNull(message = "시청 시간은 필수입니다.")
        @PositiveOrZero(message = "시청 시간은 0 또는 양수여야 합니다.")
        Integer watchedDuration
) {
}
