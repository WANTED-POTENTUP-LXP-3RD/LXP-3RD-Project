package com.lxp.aplus.progress.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.ProgressResultCode;

import com.lxp.aplus.progress.application.usecase.ProgressCommandUseCase;
import com.lxp.aplus.progress.presentation.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import jakarta.validation.Valid; // Added import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/{enrollmentId}")
    public ResponseEntity<ResultResponse<ProgressUpdateResponse>> updateProgress(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody ProgressUpdateRequest request
    ) {
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(enrollmentId, request);

        return ResponseEntity.ok(ResultResponse.of(ProgressResultCode.UPDATE_PROGRESS_SUCCESS, response));
    }
}
