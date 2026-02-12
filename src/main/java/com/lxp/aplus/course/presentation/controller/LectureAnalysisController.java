package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.course.application.result.LectureAnalysisStartResult;
import com.lxp.aplus.course.application.usecase.LectureAnalysisCommandUseCase;
import com.lxp.aplus.course.presentation.response.LectureAnalysisStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureAnalysisController {
    private final LectureAnalysisCommandUseCase lectureAnalysisCommandUseCase;

    @PostMapping("/lectures/{lectureResourceId}/analysis/start")
    public ResponseEntity<ResultResponse<LectureAnalysisStartResponse>> startAnalysis(
            @PathVariable Long lectureResourceId
    ) {
        LectureAnalysisStartResult result = lectureAnalysisCommandUseCase.startAnalysis(lectureResourceId);

        return ResponseEntity
                .status(LectureResultCode.LECTURE_ANALYSIS_START_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_ANALYSIS_START_SUCCESS,
                        LectureAnalysisStartResponse.from(result)
                ));
    }
}
