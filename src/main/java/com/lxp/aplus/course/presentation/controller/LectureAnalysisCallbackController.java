package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.course.application.usecase.LectureAnalysisCommandUseCase;
import com.lxp.aplus.course.presentation.request.LectureAnalysisCallbackRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/v1/analysis")
@RequiredArgsConstructor
public class LectureAnalysisCallbackController {
    private final LectureAnalysisCommandUseCase lectureAnalysisCommandUseCase;

    @PostMapping("/callback")
    public ResponseEntity<ResultResponse<Void>> receiveCallback(
            @RequestHeader("X-ANALYSIS-SECRET") String secret,
            @Valid @RequestBody LectureAnalysisCallbackRequest request
    ) {
        lectureAnalysisCommandUseCase.handleCallback(secret, request.toCommand());

        return ResponseEntity
                .status(LectureResultCode.LECTURE_ANALYSIS_CALLBACK_SUCCESS.getStatus())
                .body(ResultResponse.from(LectureResultCode.LECTURE_ANALYSIS_CALLBACK_SUCCESS));
    }
}
