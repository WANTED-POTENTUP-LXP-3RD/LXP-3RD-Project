package com.lxp.aplus.review.application.port.out;

import com.lxp.aplus.review.application.command.ReviewAnalyzeSaveCommand;

public interface ReviewAnalyzeSavePort {
    void save(ReviewAnalyzeSaveCommand command);
}
