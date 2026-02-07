package com.lxp.aplus.progress.adapter.in.web.controller;

import com.lxp.aplus.common.result.code.ProgressResultCode;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.progress.application.dto.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.application.dto.response.CourseProgressResponse;
import com.lxp.aplus.progress.application.dto.response.ProgressUpdateResponse;
import com.lxp.aplus.progress.application.port.in.ProgressCommandPort;
import com.lxp.aplus.progress.application.port.in.ProgressQueryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progresses")
public class ProgressController {

    private final ProgressQueryPort progressQueryPort;
    private final ProgressCommandPort progressCommandPort;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<CourseProgressResponse>> getCourseProgress(
            @Authenticated Long userId,
            @PathVariable Long courseId
    ) {
        CourseProgressResponse response = progressQueryPort.getCourseProgress(userId, courseId);
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.GET_PROGRESS_SUCCESS, response));
    }

    @PatchMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<ProgressUpdateResponse>> updateProgress(
            @Authenticated Long userId,
            @PathVariable Long courseId,
            @RequestBody @Valid ProgressUpdateRequest request
    ) {
        ProgressUpdateResponse response = progressCommandPort.updateProgress(userId, courseId, request.toCommand());
        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.UPDATE_PROGRESS_SUCCESS, response));
    }
}
