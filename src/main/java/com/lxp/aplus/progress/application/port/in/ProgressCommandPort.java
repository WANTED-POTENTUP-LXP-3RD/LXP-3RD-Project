package com.lxp.aplus.progress.application.port.in;

import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.application.dto.response.ProgressUpdateResponse;

public interface ProgressCommandPort {
    ProgressUpdateResponse updateProgress(Long userId, Long courseId, ProgressUpdateCommand command);
}
