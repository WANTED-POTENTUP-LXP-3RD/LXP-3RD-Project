package com.lxp.aplus.progress.application.dto.request;

import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProgressUpdateRequest(
        @NotNull(message = "리소스 ID는 필수입니다.")
        Long resourceId,

        @NotNull(message = "시청 시간은 필수입니다.")
        @PositiveOrZero(message = "시청 시간은 0 이상이어야 합니다.")
        Integer watchedDuration
) {
    public ProgressUpdateCommand toCommand() {
        return new ProgressUpdateCommand(resourceId, watchedDuration);
    }
}
