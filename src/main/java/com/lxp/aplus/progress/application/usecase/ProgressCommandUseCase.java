package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;

public interface ProgressCommandUseCase {
    ProgressUpdateResponse updateProgress(ProgressUpdateCommand command);
}
