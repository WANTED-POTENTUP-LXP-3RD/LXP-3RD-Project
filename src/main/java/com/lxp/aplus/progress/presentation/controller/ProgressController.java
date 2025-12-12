package com.lxp.aplus.progress.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.ProgressResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.progress.application.result.LearningHistoryResult;
import com.lxp.aplus.progress.application.usecase.ProgressCommandUseCase;
import com.lxp.aplus.progress.application.usecase.ProgressQueryUseCase;
import com.lxp.aplus.progress.presentation.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.presentation.response.LearningHistoryResponse;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progresses")
public class ProgressController {

    private final ProgressCommandUseCase progressCommandUseCase;
    private final ProgressQueryUseCase progressQueryUseCase;

    @PatchMapping("/{enrollmentId}")
    public ResponseEntity<ResultResponse<ProgressUpdateResponse>> updateProgress(
            @Authenticated UserInfo currentUser,
            @PathVariable Long enrollmentId,
            @Valid @RequestBody ProgressUpdateRequest request
    ) {
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(currentUser, enrollmentId, request);
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.UPDATE_PROGRESS_SUCCESS, response));
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<ResultResponse<LearningHistoryResponse>> getLearningHistory(
            @Authenticated UserInfo currentUser,
            @PathVariable Long enrollmentId
    ) {
        LearningHistoryResult result = progressQueryUseCase.getLearningHistory(currentUser.id(), enrollmentId);
        return ResponseEntity.ok(ResultResponse
                .of(ProgressResultCode.GET_PROGRESS_SUCCESS, LearningHistoryResponse.from(result)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<LearningHistoryResponse>> getLearningHistoryByCourseId(
            @Authenticated UserInfo currentUser,
            @PathVariable Long courseId
    ) {
        LearningHistoryResult result = progressQueryUseCase.getLearningHistoryByCourseId(currentUser.id(), courseId);
        return ResponseEntity.ok(ResultResponse
                .of(ProgressResultCode.GET_PROGRESS_SUCCESS, LearningHistoryResponse.from(result)));
    }

    @PatchMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<ProgressUpdateResponse>> updateProgressByCourseId(
            @Authenticated UserInfo currentUser,
            @PathVariable Long courseId,
            @Valid @RequestBody ProgressUpdateRequest request
    ) {
        ProgressUpdateResponse response = progressCommandUseCase.updateProgressByCourseId(currentUser, courseId, request);
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.UPDATE_PROGRESS_SUCCESS, response));
    }
}
