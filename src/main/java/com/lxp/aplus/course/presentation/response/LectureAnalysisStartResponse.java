package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.LectureAnalysisStartResult;
import com.lxp.aplus.course.domain.AnalysisStatus;
import lombok.Builder;

@Builder
public record LectureAnalysisStartResponse(
        AnalysisStatus status
) {
    public static LectureAnalysisStartResponse from(LectureAnalysisStartResult result) {
        return LectureAnalysisStartResponse.builder()
                .status(result.status())
                .build();
    }
}
