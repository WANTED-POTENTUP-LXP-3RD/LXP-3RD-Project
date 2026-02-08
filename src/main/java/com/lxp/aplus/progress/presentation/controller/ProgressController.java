package com.lxp.aplus.progress.presentation.controller;

import com.lxp.aplus.common.result.code.ProgressResultCode;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.progress.application.dto.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.application.dto.response.CourseProgressResponse;
import com.lxp.aplus.progress.application.dto.response.ProgressUpdateResponse;
import com.lxp.aplus.progress.application.port.in.ProgressCommandUseCase;
import com.lxp.aplus.progress.application.port.in.ProgressQueryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progresses")
public class ProgressController {

    private final ProgressQueryUseCase progressQueryUseCase;
    private final ProgressCommandUseCase progressCommandUseCase;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<CourseProgressResponse>> getCourseProgress(
            @Authenticated Long userId,
            @PathVariable Long courseId
    ) {
        CourseProgressResponse response = progressQueryUseCase.getCourseProgress(userId, courseId);
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.GET_PROGRESS_SUCCESS, response));
    }

    @PatchMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<ProgressUpdateResponse>> updateProgress(
            @Authenticated Long userId,
            @PathVariable Long courseId,
            @RequestBody @Valid ProgressUpdateRequest request
    ) {
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(userId, courseId, request.toCommand());
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.UPDATE_PROGRESS_SUCCESS, response));
    }
}
