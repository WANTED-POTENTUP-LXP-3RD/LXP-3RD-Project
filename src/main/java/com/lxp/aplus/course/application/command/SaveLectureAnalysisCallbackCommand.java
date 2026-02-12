package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.domain.AnalysisStatus;

import java.math.BigDecimal;
import java.util.List;

public record SaveLectureAnalysisCallbackCommand(
        Long lectureResourceId,
        String requestId,
        AnalysisStatus status,
        List<KeywordCommand> keywords,
        String errorMessage
) {
    public record KeywordCommand(
            String keyword,
            BigDecimal importance
    ) {
    }
}
