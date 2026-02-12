package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.AnalysisStatus;
import lombok.Builder;

@Builder
public record LectureAnalysisStartResult(
        AnalysisStatus status
) {
    public static LectureAnalysisStartResult from(AnalysisStatus status) {
        return LectureAnalysisStartResult.builder()
                .status(status)
                .build();
    }
}
